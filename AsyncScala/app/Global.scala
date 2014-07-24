package com.mariussoutier.example

import actors.CounterActor
import akka.actor.Cancellable
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.{Application, GlobalSettings}

import scala.concurrent.duration._
import scala.util.Random

object Global extends GlobalSettings {

  var scheduled: Cancellable = _
  val scheduledName = "ScheduledCounterActor"

  override def onStart(app: Application): Unit = {
    val system = Akka.system(app)
    val actor = system.actorOf(CounterActor.props, scheduledName)
    // After 1 seconds, send the actor a message, and then again every 10 seconds
    scheduled = system.scheduler.schedule(1.second, 10.seconds, actor, selectMessage)
  }

  private def selectMessage =
    if (Random.nextBoolean()) CounterActor.Increase else CounterActor.Decrease

  override def onStop(app: Application): Unit = scheduled.cancel()
}
