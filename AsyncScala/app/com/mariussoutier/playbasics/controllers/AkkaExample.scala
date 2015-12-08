package com.mariussoutier.playbasics.controllers

import akka.actor.{ActorRef, ActorRefFactory}
import akka.pattern.ask
import akka.util.Timeout
import com.mariussoutier.playbasics.actors.CounterActor
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * 3. Akka
  * Useful for problems that lend themselves to modelling with actors, for everything distributed, scheduled tasks,
  * and WebSockets.
  */
class AkkaExample(
                   counterActor: ActorRef, // variant 1: inject ActorRef
                   actorRefFactory: ActorRefFactory) // variant 2: inject ActorRefFactory (aka ActorSystem)
                 (implicit ec: ExecutionContext)
  extends Controller {

  // As long as Play controllers are not actors themselves, we need to use the ask pattern
  // When the actor sends a message back to the controller, the ask is fulfilled

  // Each ask needs a timeout, so we don't wait indefinitely
  implicit val timeout = Timeout(5.seconds)

  // Ask returns a future, and mapping over a future requires an ExecutionContext

  def computation() = Action.async {
    // Creates a new instance of the actor in Play's actor system
    val actor = actorRefFactory.actorOf(CounterActor.props)
    // We can now send it messages
    actor ! CounterActor.Increase
    actor ! CounterActor.Increase
    actor ! CounterActor.Increase
    actor ! CounterActor.Decrease
    actor ! CounterActor.Increase
    // Finally we want to know what the current count is
    (actor ? CounterActor.Status).mapTo[Int].map { counter =>
      Ok(s"Current counter value is $counter (should be 3)")
    }
  }

  // We can select an already running actor, in this case our own scheduled actor
  def askScheduledState() = Action.async {
    (counterActor ? CounterActor.Status).mapTo[Int].map { counter =>
      Ok(s"Current counter value is $counter")
    }
  }

  // A special goodie in Play 2.3 is WebSocket support by just sending messages between an actor and a controller
  // This feels very natural in contrast to Iteratees

  /*def webSocket() = WebSocket.acceptWithActor[String, String] { request => out =>

  }*/

}
