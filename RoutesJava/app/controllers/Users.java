package controllers;

import play.*;
import play.mvc.*;
import static play.mvc.Results.*;
import play.data.*;
import play.libs.*;
import views.html.*;
import java.util.*;

import static play.libs.F.*;

public class Users extends Controller {

  // CRUD

  public static Result showAll(String sortBy, String sortDir, Integer page) {
    return ok("Users sorted by %s, direction %s, page %d".format(sortBy, sortDir, page));
  }

  public static Result show(Long id) {
    return ok("Showing user " + id);
  }

  public static Result create() {
    return ok("User created");
  }

  public static Result update(Long id) {
    return ok("Updated " + id);
  }

  public static Result delete(Long id) {
    return ok("Deleted " + id);
  }

  // RegEx

  public static Result userImage(String username, Integer width, Integer height) {
    return ok(String.format("<img width=\"%d\" height=\"%d\" title=\"User Image for %s\" />", width, height, username));
  }

  // Value types

  public static Result setBoolPreference(String key, Boolean value) {
    return ok("Prefence %s set to %s".format(key, value.toString()));
  }

  public static Result setLongPreference(String key, Long value) {
    return ok("Prefence %s set to %s".format(key, value.toString()));
  }

  public static Result setIntPreference(String key, Integer value) {
    return ok("Prefence %s set to %s".format(key, value.toString()));
  }

}
