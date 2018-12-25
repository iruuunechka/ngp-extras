package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp

import java.io._
import java.util.{List => JList, _}

import org.uncommons.maths.random.Probability
import org.uncommons.watchmaker.framework.FitnessEvaluator
import ru.ifmo.ctd.ngp.demo.ffchooser.{StepsCondition, ValueCondition}
import ru.ifmo.ctd.ngp.demo.ffchooser.jobshop._
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.agent.NonstationaryAgent
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.reward.MultiSingleDiffReward
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state._
import ru.ifmo.ctd.ngp.demo.util.RunMany
import ru.ifmo.ctd.ngp.learning.reinforce.q.DelayedAgent
import ru.ifmo.ctd.ngp.util.FastRandom

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Runner extends App {
  val p = new Properties()
  p.load(new FileReader("./misc/jobshop.properties"))
  val reader = new DataFileReader(new File("./misc/jobshop.txt"))
  val sets = new StringTokenizer(p.getProperty("datasets.table"), ", ")
  var problems = new ListBuffer[(String, Int)]
  while (sets.hasMoreTokens) {
    problems += ((sets.nextToken(), Integer.valueOf(sets.nextToken())))
  }

  val evals = 200 * 100
  val population = 100
  val crossoverProbability = 0.8
  val generations = evals / population
  val runner = new RunMany[Double](200)
  var nameRes = mutable.Map[String, Int]()
  for (problemName <- problems) {
    val problem = reader.get(problemName._1)
	  val times = problem.getTimes
    val machines = problem.getMachines
    val jobs = times.length
    val max = jobs * JobShopUtils.sumTimes(times)
    //val helpersCount = 5;

    val flowtime = {
      val res = Array.ofDim[Double](jobs + 1)
      var sum = 0
      for (j <- 0 until jobs; k <- times(j).indices) {
        res(j + 1) += times(j)(k)
        sum += times(j)(k)
      }
      res(0) = sum
      res
    }


    val sortedByFlowtime = {
      flowtime.zipWithIndex.sortBy(_._1).map(_._2 - 1)
    }

    for (helpersCount : Int <- Seq(2)) {
      val jobPerHelper = jobs / helpersCount
//      val optima = {
//        val res = Array.fill[Double](helpersCount + 1)(max)
//        for (i <- 0 until helpersCount) {
//          for (j <- i * jobPerHelper to (i + 1) * jobPerHelper + {if (i == helpersCount - 1) jobs - jobPerHelper * helpersCount else 0} ) {
//            res(i + 1) -= flowtime(j)
//          }
//        }
//        res
//      }
      val evaluators: JList[FitnessEvaluator[JList[Integer]]] = (0 until helpersCount).map(j => new MultiFlowTimeFitness(
          sortedByFlowtime.slice(j * jobPerHelper, (j + 1) * jobPerHelper + {if (j == helpersCount - 1) jobs - jobPerHelper * helpersCount else 0})
          , max, times, machines) : FitnessEvaluator[JList[Integer]]).asJava
      val factory = new JobShopFactory(jobs, times(0).length)
      val targetFitness = new FlowTimeFitness(max, times, machines)

      for (
          agent <- Seq(() => new NonstationaryAgent[String, Integer](0.9, 0.7, 0.1, 0.5));//new DelayedAgent[String, Integer](0, 0.7, 5, 0.1)); //
          mutation <- Seq(new PositionBasedMutation());
          algo <- Seq(() //=> new PesaIIMulticriteria(targetFitness, evaluators, mutation, new GeneralizedOrderCrossover(new Probability(crossoverProbability), jobs),
                  //            crossoverProbability, factory, population, 80, grid, FastRandom.threadLocal()
//          => new NSGA2Multicriteria(targetFitness,evaluators,
//              factory, mutation, new GeneralizedOrderCrossover(new Probability(crossoverProbability), jobs), crossoverProbability, population, FastRandom.threadLocal()
            => new NSGA2MulticriteriaSlow(targetFitness,evaluators,
              factory, mutation, new GeneralizedOrderCrossover(new Probability(crossoverProbability), jobs), crossoverProbability, population, FastRandom.threadLocal()
          ))) {
//          val aa = algo()
          for (
            reward <- Seq(
//              new FitnessDiffReward,
//              new FitnessParetoDiffReward
//              new MultiPositiveFullReward(0)
//              new MultiPositiveFullReward(0.1),
                new MultiSingleDiffReward
            );
            state <- Seq[MulticriteriaOptimizationAlgorithm => MultiStateCalculator[String, Integer]](
//                    _=> new HelperState()
                    _=> new TimeIntervalState(134, 3),
//                  _=> new TimeIntervalState(134, 5),
//                  _=> new FitnessIntervalState(problemName._2, max, 3),
//              _=> new FitnessIntervalState(problemName._2, max, 5)
                    _ => new SingleState
  //              _ => new InternalState(optima),
  //              _ => new ParetoBasedState(optima),
  //              aa => new VectorInternalHelpMaxState(aa.parametersCount, aa),
  //              aa => new VectorInternalTargMaxState(aa.parametersCount, aa),
  //              aa => new VectorParetoHelpMaxState(aa.parametersCount, aa),
  //              aa => new VectorParetoTargMaxState(aa.parametersCount, aa)
            )
          ) {
            val agentName = s"${problemName._1}--${algo().getName}--${state(algo()).getName}--${reward.getName}--${agent().toString}--crossove--$crossoverProbability--population--$population--helperscount--$helpersCount"
            nameRes += ((agentName, problemName._2))
            runner += (agentName, pw => {
              val algorithm = algo()
              val env = new MultiOAEnvironment(algorithm, reward, state(algorithm))
              env.setTargetCondition(new ValueCondition(max), new StepsCondition(generations))
              //val agent =  new SoftMaxAgent[String, Integer](helpersCount, alpha, gamma, algorithm)
              //agent.learn(env)
              agent().learn(env)
              val res = max - env.getLastValues.get(0)
              pw.println(s"$agentName => $res")
              res
            })
//            val alpha = 0.6
//            val gamma = 0.7
//            val eps = 0.25
//            val alg = algo()
//            //SoftMax
//            val agentName = Array[String](
//              s"${problemName._1}--${alg.getName}--${state(alg).getName}--${reward.getName}--${new SoftMaxAgent[String, Integer](helpersCount, alpha, gamma, alg).toString}--crossove--$crossoverProbability--population--$population--helperscount--$helpersCount",
//              s"${problemName._1}--${alg.getName}--${state(alg).getName}--${reward.getName}--${new MyEGreedyAgent[String, Integer](helpersCount, alpha, gamma, eps, alg).toString}--crossover--$crossoverProbability--population--$population--helperscount--$helpersCount",
//              s"${problemName._1}--${alg.getName}--${state(alg).getName}--${reward.getName}--${new EqualProbabilityAgent[String, Integer](helpersCount, alg).toString}--crossover--$crossoverProbability--population--$population--helperscount--$helpersCount",
//              s"${problemName._1}--${alg.getName}--${state(alg).getName}--${reward.getName}--${new RandomAgent[String, Integer](helpersCount, alg).toString}--crossover--$crossoverProbability--population--$population--helperscount--$helpersCount"
//            )
//            val softMaxName = agentName(0)
//            nameRes += ((softMaxName, problemName._2))
//            runner += (softMaxName, pw => {
//              val algorithm = algo()
//              val env = new MultiOAEnvironment(algorithm, reward, state(algorithm))
//              env.setTargetCondition(new ValueCondition(max), new StepsCondition(generations))
//              val agent =  new SoftMaxAgent[String, Integer](helpersCount, alpha, gamma, algorithm)
//              agent.learn(env)
//              //agent().learn(env)
//              val res = max - env.getLastValues.get(0)
//              pw.println(s"$softMaxName => $res")
//              res
//            })
//            //MyEGreedy
//            val GreedyName = agentName(1)
//            nameRes += ((GreedyName, problemName._2))
//            runner += (GreedyName, pw => {
//              val algorithm = algo()
//              val env = new MultiOAEnvironment(algorithm, reward, state(algorithm))
//              env.setTargetCondition(new ValueCondition(max), new StepsCondition(generations))
//              val agent =  new MyEGreedyAgent[String, Integer](helpersCount, alpha, gamma, eps, algorithm)
//              agent.learn(env)
//              //agent().learn(env)
//              val res = max - env.getLastValues.get(0)
//              pw.println(s"$GreedyName => $res")
//              res
//            })
//            //EqualProbability
//            val EqualName = agentName(2)
//            nameRes += ((EqualName, problemName._2))
//            runner += (EqualName, pw => {
//              val algorithm = algo()
//              val env = new MultiOAEnvironment(algorithm, reward, state(algorithm))
//              env.setTargetCondition(new ValueCondition(max), new StepsCondition(generations))
//              val agent =  new EqualProbabilityAgent[String, Integer](helpersCount, algorithm)
//              agent.learn(env)
//              //agent().learn(env)
//              val res = max - env.getLastValues.get(0)
//              pw.println(s"$EqualName => $res")
//              res
//            })
//            val RandomName = agentName(3)
//            nameRes += ((RandomName, problemName._2))
//            runner += (RandomName, pw => {
//              val algorithm = algo()
//              val env = new MultiOAEnvironment(algorithm, reward, state(algorithm))
//              env.setTargetCondition(new ValueCondition(max), new StepsCondition(generations))
//              val agent =  new RandomAgent[String, Integer](helpersCount, algorithm)
//              agent.learn(env)
//              //agent().learn(env)
//              val res = max - env.getLastValues.get(0)
//              pw.println(s"$RandomName => $res")
//              res
//            })
          }
        }
     }
  }
  
  val fileOut = new FileWriter("./irene-resultstimes10-100fullv1.txt")
  val stdOut = new OutputStreamWriter(System.out)
  for ((name, result) <- runner.runToWriters(fileOut, stdOut)) {
    val sum = result.sum
    val sum2 = result.map(i => i * i).sum
    val mean = sum / result.size
    val dev = math.sqrt((sum2 / result.size) - mean * mean)
    val percent = (mean - nameRes(name)) / nameRes(name) * 100
    val finalRes = s"$name-- percent = $percent mean = $mean min = ${result.min} max = ${result.max} dev = $dev"
    fileOut.append(finalRes + "\n")
    stdOut.append(finalRes + "\n")
  }
  fileOut.close()
  stdOut.close()
}
