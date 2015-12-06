package controllers

import javax.inject.Inject

import com.mariussoutier.playbasics.components.DatabaseClientApi
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

/**
 *
 * @param ws WSClient will get injected,
 */
class Application @Inject() (ws: WSClient, dbClient: DatabaseClientApi, implicit val ec: ExecutionContext) extends Controller {

  // Application will be instantiated each time the app is reloaded
  println("Hi from Application!")

  def index() = Action {
    Ok("It works")
  }

  def play() = Action.async {
    ws.url("http://www.playframework.com").get().map { response =>
      Ok(response.body).as(HTML)
    }
  }

  def user(id: String) = Action.async {
    dbClient.databaseClient.query(s"{id:$id}").map(user => Ok(Json.parse(user)))
  }
}
