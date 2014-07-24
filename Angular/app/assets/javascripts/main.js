(function() {
  "use strict";

  requirejs.config({
    shim: {
      'jsRoutes': {
        deps: [],
        // it's not a RequireJS module, so we have to tell it what var is returned
        exports: 'jsRoutes'
      },
      // Hopefully this all will not be necessary but can be fetched from WebJars in the future
      'angular': {
        deps: ['jquery'],
        exports: 'angular'
      }
    },
    paths: {
      'requirejs': ['../lib/requirejs/require'],
      'jquery': ['../lib/jquery/jquery'],
      'angular': ['../lib/angularjs/angular'],
      'jsRoutes': ['/jsroutes']
    }
  });

  require(["angular", "./services/playRoutes", "./controllers/login"], function(a, b, login) {
    var app = angular.module("app", ["play.routing"]);
    app.controller("LoginCtrl", login.LoginCtrl);

    angular.bootstrap(document, ["app"]);
});
})();
