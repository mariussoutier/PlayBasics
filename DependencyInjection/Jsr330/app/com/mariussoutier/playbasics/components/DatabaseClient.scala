package com.mariussoutier.playbasics.components

import javax.inject.{Provider, Inject, Singleton}

import com.google.inject.ImplementedBy
import db.DatabaseClient
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Environment, Mode}

import scala.concurrent.Future

// @ImplementedBy determines the actual implementation
@ImplementedBy(classOf[DatabaseClientDefaultImpl])
trait DatabaseClientApi {
  def databaseClient: DatabaseClient
}

// ServiceClient must extend the trait or a runtime exception will be thrown
@Singleton
class DatabaseClientDefaultImpl @Inject()(environment: Environment,
                                          configuration: Configuration,
                                          applicationLifecycle: ApplicationLifecycle)
  extends DatabaseClientApi {

  // Instantiated client to our imaginary service
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


@Singleton
class DatabaseClientProvider @Inject()(environment: Environment,
                                       configuration: Configuration,
                                       applicationLifecycle: ApplicationLifecycle)
  extends Provider[DatabaseClient] {

  override def get(): DatabaseClient = {
    val client = environment.mode match {
      case Mode.Test =>
        DatabaseClient.inMemory()
      case _ =>
        new DatabaseClient(configuration.getString("external-service.host").get)
    }
    applicationLifecycle.addStopHook(() => Future.successful(client.close()))
    client
  }
}
