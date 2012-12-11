package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import static play.mvc.Results.*;

import play.i18n.Lang;
import play.libs.F.Option;

public class Help {

  public static Result show(String language) {
    return ok(localized.render("faq", Lang.forCode(language)));
  }

}
