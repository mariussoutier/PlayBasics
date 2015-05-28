package controllers

import javax.inject.Inject

import play.api.i18n._
import play.api.mvc._

class Help @Inject() (val messagesApi: MessagesApi) extends Controller with Localized with I18nSupport {

  def show(language: String) = ActionWithLanguage(language) { implicit request =>
    // Partially applied route so the app can fill in the language parameter
    val route = routes.Help.show _
    Ok(views.html.localized("help", Some(route)))
  }

}
