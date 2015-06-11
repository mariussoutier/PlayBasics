package controllers

import javax.inject.Inject

import play.api.i18n._
import play.api.mvc._

class Faq @Inject() (val messagesApi: MessagesApi, val langs: Langs) extends Controller with Localized {

  def show(language: Lang) = ActionWithLang(language) { implicit request =>
    Ok("%s - Language: %s, Country: %s".format(Messages("faq"), language.language, language.country))
  }

}
