package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._

object Faq extends Controller with Localized {

  def show(language: Lang) = ActionWithLang(language) { implicit request =>
    Ok("%s - Language: %s, Country: %s".format(Messages("faq"), lang.language, lang.country))
  }

}
