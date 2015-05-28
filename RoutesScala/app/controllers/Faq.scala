package controllers

import javax.inject.Inject

import play.api.i18n._
import play.api.mvc._

class Faq @Inject() (val messagesApi: MessagesApi) extends Controller with Localized with I18nSupport {

  def show(language: Lang) = ActionWithLang(language) { implicit request =>
    Ok("%s - Language: %s, Country: %s".format(Messages("faq"), lang.language, lang.country))
  }

}
