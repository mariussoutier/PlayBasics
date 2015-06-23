package components

import com.google.inject.ImplementedBy

// @ImplementedBy determines the actual implementation
@ImplementedBy(classOf[DatabaseClientImpl])
trait DatabaseClient {
  def stop(): Unit
}

// DatabaseClientImpl must extend the trait or a runtime exception will be thrown
class DatabaseClientImpl extends DatabaseClient {
  var stopped = false

  def stop(): Unit = {
    if (stopped) {
      throw new IllegalArgumentException("Client was already stopped!")
    }
    stopped = true
  }

}
