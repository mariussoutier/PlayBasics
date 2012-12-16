package views.html.helper

import play.api.templates.Html

/** Not everything can be solved with a template. Sometimes you need a Scala object, e.g. when
you want to use generics */

object repeatWithIndex {
  def apply(field: play.api.data.Field, min: Int = 1)(f: (play.api.data.Field, Int) => Html) = {
    (0 until math.max(if (field.indexes.isEmpty) 0 else field.indexes.max + 1, min)).map(i => f(field("[" + i + "]"), i))
  }
}

object commaSeparated {
  /* Takes String components, filters nulls, joins them using "," */
  def apply(components: String*) = components.filter(_ != null).mkString(", ")
}
