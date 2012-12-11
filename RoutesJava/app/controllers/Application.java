package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import play.libs.F.Option;

import java.util.*;

public class Application extends Controller {

  public static Result index(Option<String> ref) {
    if (ref.isDefined()) {
      String refCode = ref.get();
      response().setCookie("REFERRAL", refCode, 60*60);
    }
    return ok(index.render("Welcome to Play Basics - Routes for Java"));
  }

  public static Result jsRoutes() {
    response().setContentType("text/javascript");
    return ok(Routes.javascriptRouter("jsRoutes", routes.javascript.Users.delete()));
  }

  public static List<String> languages() {
    String[] langs = Play.application().configuration().getString("application.langs").split(",");
    return Arrays.asList(langs);
  }

}
