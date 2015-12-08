package controllers

import db.DatabaseClient
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

/**
  * Dependencies, in the sense of "class instances we depend on", are now passed in the constructor and no longer
  * instantiated in the controller. Our controller only wants to use them anyway, and we avoid putting any logic in
  * a controller that doesn't belong here. We don't even need to read anything from the Configuration or
  * play.api.Application.
  */
class Application(ws: WSClient, databaseClient: DatabaseClient)(implicit val ec: ExecutionContext) extends Controller {

  // Application will be instantiated each time the app is reloaded
  println("Hi from Application!")

  def index() = Action {
    Ok("It works")
  }

  // This action requires an external component, i.e. the WS service
  // Notice how we no longer have to deal with the current play.api.Application as in previous versions of Play
  def play() = Action.async {
    ws.url("http://www.playframework.com").get().map { response =>
      Ok(response.body).as(HTML)
    }
  }

  def user(id: String) = Action.async {
    databaseClient.query(s"{id:$id}").map(user => Ok(Json.parse(user)))
  }
}
