package com.mariussoutier.playbasics.components

import db.DatabaseClient
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.{Json, JsValue}
import play.api.{Mode, Configuration, Environment}

import scala.concurrent.Future

/**
 * Plugins have been replaced with components. Here we wrap an imaginary service.
 */
trait DatabaseClientComponents {
  // These will be filled by Play's built-in components; should be `def` to avoid initialization problems
  def environment: Environment
  def configuration: Configuration
  def applicationLifecycle: ApplicationLifecycle

  // Instantiated client to our imaginary service, should be lazy
  // also careful with naming, it might collide with other names, so consider using wrapper objects as in Cake pattern
  lazy val databaseClient: DatabaseClient = {
    val client = environment.mode match {
      case Mode.Test =>
        // Let's pretend there's an in-memory version that we'd use in our tests
        DatabaseClient.inMemory()
      case _ =>
        new DatabaseClient(configuration.getString("external-service.host").get)
    }
    // Shutdown the client when the app is stopped or reloaded
    applicationLifecycle.addStopHook(() => Future.successful(client.close()))
    client
  }
}
