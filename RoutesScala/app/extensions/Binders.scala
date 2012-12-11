package extensions

import play.api.mvc._
import play.api.i18n.Lang

object Binders {

  // Type declaration is needed so it's recognized in the routes file
  type Lang = play.api.i18n.Lang

  implicit object LangQueryStringBindable extends QueryStringBindable[Lang] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Lang]] = params.get(key).flatMap(_.headOption).map { _ match {
        case str: String if str.length == 5 && str.contains("-") => {
          val components = str.split('-')
          Right(new Lang(components(0), components(1)))
        }
        case str: String if (str.length == 2) => Right(new Lang(str))
        case _ => Left("Cannot parse parameter '" + key + "' as Lang")
      }
    }

    def unbind(key: String, value: Lang): String = key + "=" + value.code
  }

  implicit object LangPathBindable extends PathBindable[Lang] {
    def bind(key: String, value: String) = try {
      Right(Lang(value))
    } catch {
      case e: Exception => Left("Cannot parse parameter '" + key + "' as Lang")
    }

    def unbind(key: String, value: Lang): String = value.code
  }

  implicit def langJavascriptLitteral = new JavascriptLitteral[Lang] {
    def to(value: Lang) = value.code
  }
}
