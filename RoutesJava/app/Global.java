import play.*;
import play.mvc.*;
import static play.mvc.Results.*;

import java.util.*;

import controllers.routes;


public class Global extends GlobalSettings {

  @Override
  public void onStart(Application app) {
    System.out.println(routes.Users.show(1234).url());
  }

  // Taken from an example by James Roper, member of the Play team
  @Override
  public Result onHandlerNotFound(Http.RequestHeader request) {
    if (request.path().endsWith("/"))
      return movedPermanently(request.path().substring(0, request.path().length() - 1));
    else
      return super.onHandlerNotFound(request);
  }
}
