import java.io.File

import com.mariussoutier.playbasics.AppComponents
import db.DatabaseClient
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api
import play.api.ApplicationLoader.Context
import play.api.http.MimeTypes
import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.ning.NingWSComponents
import play.api.routing.Router
import play.api.test.FakeRequest
import play.api.{BuiltInComponentsFromContext, ApplicationLoader, Environment, Mode}
import play.api.test.Helpers._
import router.Routes

import scala.concurrent.{ExecutionContext, Future}

object TestData {
  def expectedUser = Json.obj("id" -> "12345")
}

class FakeDatabaseClient extends DatabaseClient("fake") {
  override def query(queryString: String)(implicit ec: ExecutionContext): Future[String] =
    Future.successful(TestData.expectedUser.toString())

  override def close(): Unit = ()
}

class FakeApplicationComponents(context: Context) extends AppComponents(context) {
  override lazy val applicationController = new controllers.Application(wsClient, new FakeDatabaseClient)
}

class FakeAppLoader extends ApplicationLoader {
  override def load(context: Context): api.Application =
    new FakeApplicationComponents(context).application
}

class ApplicationTest extends PlaySpec with OneAppPerSuite {
  override implicit lazy val app: api.Application = {
    val appLoader = new FakeAppLoader
    val context = ApplicationLoader.createContext(
      new Environment(new File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test)
    )
    appLoader.load(context)
  }

  // This is more like an integration test as it tests the real thing
  "check /play route proxying playframework.com" in {
    val playResult = route(FakeRequest(GET, "/play")).get
    status(playResult) mustEqual OK
    contentType(playResult) mustEqual Some(MimeTypes.HTML)
    contentAsString(playResult) must include("Play Framework")
  }

  "check fetch user" in {
    val id = "12345" // you could also generate random ids using ScalaCheck
    val userResult = route(FakeRequest(GET, s"/user/$id")).get
    status(userResult) mustEqual OK
    contentType(userResult) mustEqual Some(MimeTypes.JSON)
    contentAsJson(userResult) mustEqual TestData.expectedUser
  }
}
