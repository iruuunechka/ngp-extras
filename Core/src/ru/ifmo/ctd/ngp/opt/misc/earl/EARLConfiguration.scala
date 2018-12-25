package ru.ifmo.ctd.ngp.opt.misc.earl

import java.util.{Arrays => JArrays, List => JList}

import scala.language.higherKinds
import ru.ifmo.ctd.ngp.opt.event.{InitializationFinishedEvent, IterationFinishedEvent, TerminationStartedEvent}
import ru.ifmo.ctd.ngp.opt.types.{CodomainType, DomainType, WorkingSetType}
import ru.ifmo.ctd.ngp.learning.reinforce.{Agent => OAgent, Environment => OEnv, EnvironmentPrinter => OEP}
import ru.ifmo.ctd.ngp.opt.listeners.TimeFromStart

object EARLConfiguration {
  def saraWillSwitchAfter(iterations: Long, criterionCount: Int): Boolean = {
    val section = (iterations - 1) / criterionCount + 1
    // [1] [2; 3] [4; 7] [8; 15] [16; 31] ...
    val group = 63 - java.lang.Long.numberOfLeadingZeros(section)
    // [0] [1] [2] [3] ...
    val mask = (1L << group) - 1
    val previousSlots = mask * criterionCount
    val collectedDiff = iterations - previousSlots
    (collectedDiff & mask) == 0
  }

  class Detected[D: DomainType, C: CodomainType, W[+_D, +_C]: WorkingSetType] {
    /**
      * Registers a configuration based on the SaRA algorithm.
      *
      * @param order the order in which to use objectives.
      * @param resetOnInitialization whether to reset internal counters on initialization.
      */
    def registerSaRA(order: IndexedSeq[Int], resetOnInitialization: Boolean)(
      implicit initializationFinished: InitializationFinishedEvent[W[D, C]],
               iterationFinished: IterationFinishedEvent[W[D, C]],
               earlInterface: EARLInterface
    ) {
      assert(order.size == earlInterface.choices,
        s"order.size = ${order.size} while there are ${earlInterface.choices} EA+RL choices")
      var remained = 1L
      var level = 0L
      var current = 0
      if (resetOnInitialization) {
        InitializationFinishedEvent().addListener { _ =>
          level = 0
          remained = 1
          current = 0
          earlInterface.currentChoice = order(current)
        }
      }
      IterationFinishedEvent().addListener { _ =>
        remained -= 1
        if (remained == 0) {
          current += 1
          current = if (current == earlInterface.choices) {
            level += 1
            0
          } else current
          earlInterface.currentChoice = order(current)
          remained = 1L << level
        }
      }
    }

    /**
      * Registers a configuration based on the Jensen objective selection scheme.
      *
      * @param order the order in which to use objectives.
      * @param budget the evaluation budget to allocate to every objective.
      */
    def registerJensenByEvaluations(order: IndexedSeq[Int], budget: Long)(
      implicit initializationFinished: InitializationFinishedEvent[W[D, C]],
      iterationFinished: IterationFinishedEvent[W[D, C]],
      earlInterface: EARLInterface
    ) {
      assert(order.size == earlInterface.choices,
        s"order.size = ${order.size} while there are ${earlInterface.choices} EA+RL choices")
      var remained = 0L
      var index = 0
      InitializationFinishedEvent().addListener { _ =>
        remained = budget
        index = 0
        earlInterface.currentChoice = order(index)
      }
      IterationFinishedEvent().addListener { _ =>
        remained -= 1
        if (remained == 0) {
          remained = budget
          index = (index + 1) % order.size
          earlInterface.currentChoice = order(index)
        }
      }
    }

    /**
      * Registers a configuration based on the Jensen objective selection scheme.
      *
      * @param order the order in which to use objectives.
      * @param budget the time budget to allocate to every objective.
      */
    def registerJensenByTime(order: IndexedSeq[Int], budget: Long)(
      implicit initializationFinished: InitializationFinishedEvent[W[D, C]],
      iterationFinished: IterationFinishedEvent[W[D, C]],
      timeFromStart: TimeFromStart,
      earlInterface: EARLInterface
    ) {
      assert(order.size == earlInterface.choices,
        s"order.size = ${order.size} while there are ${earlInterface.choices} EA+RL choices")
      var remained = 0L
      var index = 0
      InitializationFinishedEvent().addListener { _ =>
        remained = budget + timeFromStart()
        index = 0
        earlInterface.currentChoice = order(index)
      }
      IterationFinishedEvent().addListener { _ =>
        val currentTime = timeFromStart()
        if (remained < currentTime) {
          remained = budget + currentTime
          index = (index + 1) % order.size
          earlInterface.currentChoice = order(index)
        }
      }
    }

    /**
     * Registers a configuration for EA+RL constructed from the old-fashioned RL agent, the state function
     * and the implicitly available EARLCodomainComparator, EvaluationFinishedEvent,
     * InitializationFinishedEvent and TerminationStartedEvent.
     *
     * This is a long ugly story of coping with the non-reactive design of ru.ifmo.ctd.ngp.learning.reinforce.Agent.
     *
     * @param agent the reinforcement learning agent.
     * @param stateFunction the function returning the RL state for the current working set.
     * @param rewardFunction the function returning the RL reward for the consecutive working sets.
     */
    def registerOldWay[S](
               agent: OAgent[S, Int],
               stateFunction: W[D, C] => S,
               rewardFunction: (W[D, C], W[D, C]) => Double
    )(
      implicit iterationFinished: IterationFinishedEvent[W[D, C]],
               initializationFinished: InitializationFinishedEvent[W[D, C]],
               terminationStarted: TerminationStartedEvent,
               earlInterface: EARLInterface
    ) {
      val AgentInterruption = new RuntimeException("Agent is interrupted")
      class MyEnv extends OEnv[S, Int] {
        var lastWorkingSet: W[D, C] = _
        var lastState: S = _
        var lastReward: Double = _
        var lastAction: Int = _

        val sync = new Object
        var isAgentRunning = true
        var isTerminating = false
        var agentThread: Thread = _

        def getLastAction: Int = lastAction
        def getTargetValue: Double = throw new NotImplementedError("Not implemented and maybe not needed at all")
        def getBestTargetValue: Double = throw new NotImplementedError("Not implemented and maybe not needed at all")
        def addPrinter(printer: OEP[S, Int]) {}
        def applyAction(action: Int): Double = {
          lastAction = action
          sync.synchronized {
            isAgentRunning = false
            sync.notify()
            while (!isAgentRunning && !isTerminating) {
              sync.wait()
            }
            if (isTerminating) {
              throw AgentInterruption
            }
          }
          lastReward
        }
        def getCurrentState: S = lastState
        val getActions: JList[Int] = JArrays.asList(0 until earlInterface.choices :_*)
        def actionsCount(): Int = getActions.size()
        def firstAction(): Int = getActions.get(0)
        def isInTerminalState = false
        def getLastReward: Double = lastReward

        def kill() {
          sync.synchronized {
            isTerminating = true
            sync.notify()
          }
          agentThread.join()
        }

        def waitForApplyAction() {
          sync.synchronized {
            isAgentRunning = true
            sync.notify()
            while (isAgentRunning) {
              sync.wait()
            }
          }
        }
      }

      var previousWS: Option[W[D, C]] = None
      val myAgent = agent.makeClone()
      val environment = new MyEnv

      InitializationFinishedEvent().addListener { ws =>
        myAgent.refresh()
        previousWS = Some(ws)
        environment.lastWorkingSet = ws
        environment.lastState = stateFunction(ws)
        environment.agentThread = new Thread(
          (() => {
            try {
              myAgent.learn(environment)
            } catch {
              case AgentInterruption =>
              case th: Throwable => throw th
            }
          }): Runnable, "Agent Runner"
        )
        environment.agentThread.start()
        environment.waitForApplyAction()
        earlInterface.currentChoice = environment.lastAction
      }

      IterationFinishedEvent().addListener { ws =>
        val currentState = stateFunction(ws)
        val currentReward = rewardFunction(environment.lastWorkingSet, ws)
        environment.lastWorkingSet = ws
        environment.lastState = currentState
        environment.lastReward = currentReward
        environment.waitForApplyAction()
        earlInterface.currentChoice = environment.lastAction
      }

      TerminationStartedEvent().addListener { _ =>
        environment.kill()
      }
    }
  }
  /**
   * Detects the domain, codomain, `Evaluated` object type and the working set type, and allows to select more options.
   * @tparam D the domain type.
   * @tparam C the codomain type.
   * @tparam W the working set type.
   * @return the object for more options to build an `Iteration` object.
   */
  def apply[D: DomainType, C: CodomainType, W[+_D, +_C]: WorkingSetType](): Detected[D, C, W] = new Detected
}
