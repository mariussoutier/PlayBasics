package components

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.{Json, JsValue}

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DatabaseClientRepository])
trait Repository {
  def fetchData: Future[Seq[JsValue]]
}

/**
 *
 * @param lifecycle ApplicationLifecycle requires the component to be a singleton.
 */
@Singleton
class DatabaseClientRepository @Inject()(lifecycle: ApplicationLifecycle, client: DatabaseClient, implicit val ec: ExecutionContext) extends Repository {
  lifecycle.addStopHook { () =>
    Future.successful(client.stop())
  }

  override def fetchData: Future[Seq[JsValue]] = Future.successful(Seq(Json.obj("data" -> "real")))
}
