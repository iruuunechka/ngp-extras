package ru.ifmo.ctd.ngp.demo.util

import java.io.{File, FileWriter, PrintWriter, Writer}

import scala.annotation.tailrec

/**
 * A graphical runner for may tasks, each of which prints something to the console.
 *
 * @author Maxim Buzdalov
 */
class RunMany[V](times: Int, threads: Int = Runtime.getRuntime.availableProcessors()) {
  private var jobs = List[(String, PrintWriter => V)]()

  def += (name: String, action: PrintWriter => V) {
    jobs = (name, action) :: jobs
  }

  private def invokeInParallel(writerFun: String => PrintWriter): List[(String, V)] = {
    val queue = new java.util.concurrent.LinkedBlockingQueue[(String, PrintWriter => V, Int)]()
    for (index <- 0 until times) {
      jobs.foreach { case (name, fun) => queue.add((name, fun, index + 1)) }
    }
    val results = new java.util.concurrent.LinkedBlockingQueue[(String, V)]()
    val runners = (0 until threads).map(id => new Thread(() => {
      println(s"Thread ${Thread.currentThread().getName} started.")

      @tailrec
      def runImpl() {
        val task = queue.poll()
        if (task != null) {
          val (name, fun, number) = task
          val realName = s"$name($number)"
          val pw1 = writerFun(realName)
          val outFile = new File(s"logs/$realName.log")
          outFile.getParentFile.mkdirs()
          val pw2 = new FileWriter(outFile)
          val pwAll = new PrintWriter(new SwitchWriter(pw1, pw2), true)

          val result = fun(pwAll)
          results.add((name, result))

          pwAll.close()
          pw2.close()
          pw1.close()
          runImpl()
        }
      }

      runImpl()
    }, s"RunMany[$id]"))
    runners.foreach(_.start())
    runners.foreach(_.join())
    scala.collection.JavaConverters.iterableAsScalaIterable(results).toList
  }

  private class SwitchWriter(writers: Writer*) extends Writer {
    override def write(cbuf: Array[Char], off: Int, len: Int) {
      for (w <- writers) {
        w.write(cbuf, off, len)
        w.flush()
      }
    }
    override def flush() {
      for (w <- writers) {
        w.flush()
      }
    }
    override def close() {}
  }

  private class TaggingWriter(writer: Writer, tag: String) extends Writer {
    private val chars = f"${s"[$tag]"}%30s ".toCharArray
    private var insertOnWrite = true
    override def write(cbuf: Array[Char], off: Int, len: Int) {
      def writeHelp(off: Int, len: Int) {
        if (insertOnWrite) {
          insertOnWrite = false
          writer.write(chars)
        }
        writer.write(cbuf, off, len)
      }
      def writeImpl(from: Int, curr: Int, until: Int) {
        if (curr == until) {
          writeHelp(from, until - from)
        } else if (cbuf(curr) == '\n') {
          writeHelp(from, curr + 1 - from)
          insertOnWrite = true
          writeImpl(curr + 1, curr + 1, until)
        } else {
          writeImpl(from, curr + 1, until)
        }
      }
      writeImpl(off, off, off + len)
    }
    override def flush() {
      writer.flush()
    }
    override def close() {}
  }

  def runToWriters(writers: Writer*): Map[String, List[V]] = {
    val writerToEveryone = new SwitchWriter(writers : _*)
    val results = invokeInParallel(tag => new PrintWriter(new TaggingWriter(writerToEveryone, tag), true))
    results.groupBy {
      case (name, _) => name
    } map {
      case (key, list) => (key, list.map {
        case (_, value) => value
      })
    }
  }
}
