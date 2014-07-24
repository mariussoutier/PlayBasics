package com.mariussoutier.example.ng.controllers

import play.api.libs.json._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def login() = Action(parse.json) { implicit request =>
    // Check credentials and so on...
    Ok(Json.obj("token" -> java.util.UUID.randomUUID().toString))
  }

}
