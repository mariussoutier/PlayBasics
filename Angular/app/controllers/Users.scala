package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._

object Users extends Controller {

  def user(id: Long) = Action {
    Ok(Json.obj("firstName" -> "John", "lastName" -> "Smith", "age" -> 42))
  }

}
