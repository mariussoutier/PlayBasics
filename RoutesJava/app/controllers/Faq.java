package controllers;

import play.*;
import play.mvc.*;
import views.html.*;
import play.i18n.*;

//import extensions.LangPathWrapper;
//import extensions.LangQueryWrapper;

public class Faq extends Controller {

  public static Result show(Lang lang) {
    String result = String.format("%s - Language: %s, Country: %s",
      Messages.get(lang, "faq"), lang.language(), lang.country());
    return ok(result);
  }

  // This is the action when using a Java wrapper
  /*public static Result show(LangPathWrapper langWrapper) {
    String result = String.format("%s - Language: %s, Country: %s",
      Messages.get(langWrapper.lang, "faq"), langWrapper.lang.language(), langWrapper.lang.country());
    return ok(result);
  }*/

}
