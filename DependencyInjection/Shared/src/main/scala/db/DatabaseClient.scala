package db

import scala.concurrent.{ExecutionContext, Future}

/**
 * Some (imaginary) external database client you want to integrate using DI.
 */
class DatabaseClient(host: String) {
  var stopped = false

  def query(queryString: String)(implicit ec: ExecutionContext): Future[String] = {
    // Imagine querying the database here
    Future.successful(
      """
      |{"ok":1}
    """.stripMargin
    )
  }

  def close(): Unit = {
    if (stopped) {
      throw new IllegalArgumentException("Client was already stopped!")
    }
    stopped = true
  }
}

object DatabaseClient {
  def inMemory() = new DatabaseClient("in-memory")
}
