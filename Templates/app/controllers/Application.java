package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import play.data.validation.Constraints.*;

import views.html.*;

import java.util.List;
import java.util.ArrayList;

import models.*;

public class Application extends Controller {

  public static Form<User> userForm = form(User.class);

  public static Result index() {
    //session("email", "user@gmail.com");
    final User user = User.forName("Musterbro");
    final List<User> users = new ArrayList<User>();
    users.add(user);
    return ok(views.html.index.render(users));
  }

  public static Result editProfile() {
    //if session...
    final User user = User.forName("Musterbro");
    return ok(views.html.users.editProfile.render(userForm.fill(user)));
  }

  public static Result updateProfile() {
    final User user = userForm.bindFromRequest().get();
    return redirect(routes.Application.showProfile());
  }

  public static Result exportUsers() {
    final User user = User.forName("Musterbro");
    final List<User> users = new ArrayList<User>();
    users.add(user);
    String trimmedText = views.txt.usersExport.render(users, true).body().trim();
    return ok(trimmedText);
  }

  public static Result showProfile() {
    final User user = User.forName("Musterbro");
    return ok(views.html.users.profile.render(user));
  }

}
