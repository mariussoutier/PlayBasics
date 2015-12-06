package controllers

import db.DatabaseClient
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class Application(ws: WSClient, databaseClient: DatabaseClient)(implicit val ec: ExecutionContext) extends Controller {

  // Application will be instantiated each time the route is called
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
    databaseClient.query(s"{id:$id}").map(user => Ok(Json.parse(user)))
  }
}
