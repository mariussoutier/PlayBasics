package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._

object Help extends Controller with Localized {

  def show(language: String) = ActionWithLanguage(language) { implicit request =>
    // Partially applied route so the app can fill in the language parameter
    val route = routes.Help.show _
    Ok(views.html.localized("help", Some(route)))
  }

}
