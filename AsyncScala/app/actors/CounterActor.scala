package actors

import akka.actor.{Props, Actor, ActorLogging}

/**
 * This actor just keeps track of something. You can either increase or decrease its counter, or ask for the current
 * counter. An actor only processes one message at a time, so this looks all very simple.
 */
class CounterActor extends Actor with ActorLogging {

  override def receive: Receive = withCounter(0)

  def withCounter(counter: Int): Receive = {
    case m: CounterActor.Messages => m match {
      case CounterActor.Status => sender ! counter
      // context.become allows the actor to change its state; it is reset after a crash however, we'd need a database
      // or a persistent actor to avoid this
      case CounterActor.Increase => context.become(withCounter(counter + 1))
      case CounterActor.Decrease => context.become(withCounter(counter - 1))
    }
  }

  // Init your actor here
  override def preStart(): Unit = super.preStart()

  // Cleanup any used resources here
  override def postStop(): Unit = super.postStop()
}

/**
 * The accepted messages are kept in the companion object. A common trait makes sure the compiler will warn us when we
 * forget to match a message.
 */
object CounterActor {

  sealed trait Messages

  case object Status extends Messages

  case object Increase extends Messages

  case object Decrease extends Messages

  def props: Props = Props[CounterActor]

}
