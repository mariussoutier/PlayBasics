(function() {
  "use strict";

  // Angular must be first
  requirejs.config({
    priority: ["angular"]
  });

  // Define angular as a RequireJS module so it can be referenced in other defines
  define("angular", ["webjars!angular.js"], function() {
    return angular; // return the global var
  });

  // The same mechanism can be used for external dependencies
  define("jsroutes", ["/jsroutes"], function() {
    return jsRoutes; // global again; must match var name used on the server
  });

  require(["angular", "./services/playRoutes", "./controllers/login"], function(a, b, login) {
    var app = angular.module("app", ["play.routing"]);
    app.controller("LoginCtrl", login.LoginCtrl);

    angular.bootstrap(document, ["app"]);
});
})();
