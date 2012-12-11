package extensions

import play.api.mvc._
import play.i18n.Lang

/* This is Scala but works well in a Play Java project. The Java binders are really unusable. */
object Binders {

  // Type declaration is needed so it's recognized in the routes file
  type ScalaLang = play.api.i18n.Lang
  type Lang = play.i18n.Lang

  implicit object LangQueryStringBindable extends QueryStringBindable[Lang] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Lang]] = params.get(key).flatMap(_.headOption).map { _ match {
        case str: String if (str.length == 5 || str.length == 6) && str.contains("-") => {
          Right(Lang.forCode(str))
        }
        case str: String if (str.length == 2) => Right(Lang.forCode(str))
        case _ => Left("Cannot parse parameter '" + key + "' as Lang")
      }
    }

    def unbind(key: String, value: Lang): String = key + "=" + value.code
  }

  implicit object LangPathBindable extends PathBindable[Lang] {
    def bind(key: String, value: String) = value match {
      case str: String if (str.length == 5 || str.length == 6) && str.contains("-") => {
        Right(Lang.forCode(str))
      }
      case str: String if (str.length == 2) => Right(Lang.forCode(str))
      case _ => Left("Cannot parse parameter '" + key + "' as Lang")
    }

    def unbind(key: String, value: Lang): String = value.code
  }

  implicit def langJavascriptLitteral = new JavascriptLitteral[Lang] {
    def to(value: Lang) = value.code
  }
}
