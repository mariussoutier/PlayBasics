package com.mariussoutier.example.ng.controllers

import play.api.libs.json._
import play.api.mvc._

object Users extends Controller {

  def user(id: Long) = Action {
    Ok(Json.obj("firstName" -> "John", "lastName" -> "Smith", "age" -> 42))
  }

}
