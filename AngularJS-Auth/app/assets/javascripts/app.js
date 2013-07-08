(function() {
  "use strict";

  requirejs.config({
    shim : {
      "webjars!angular-cookies.js" : ["angular"] // make angular available to ngCookies
    },
    priority: ["angular"] // Make sure angular is loaded first
  });

  // This is just for convenience so we can use the shorter module name
  define("angular", ["webjars!angular.js"], function() {
    return angular; // return global var
  });

  require(["./controllers/login", "angular", "webjars!angular-cookies.js"],
    function(login) {

    var module = angular.module("my.app", ["ngCookies"]);

    module.controller("LoginCtrl", login.LoginCtrl);

    module.config(["$httpProvider", function($httpProvider) {
      var interceptor = ["$rootScope", "$q", "$timeout", function($rootScope, $q, $timeout) {
        return function(promise) {
          return promise.then(
            function(response) {
              return response;
            },
            function(response) {
              if (response.status == 401) {
                $rootScope.$broadcast("InvalidToken");
                $rootScope.sessionExpired = true;
                $timeout(function() {$rootScope.sessionExpired = false;}, 5000);
              } else if (response.status == 403) {
                $rootScope.$broadcast("InsufficientPrivileges");
              } else {
                // Here you could handle other status codes, e.g. retry a 404
              }
              return $q.reject(response);
            }
          );
        };
      }];
      $httpProvider.responseInterceptors.push(interceptor);
    }]);

    angular.bootstrap(document, ["my.app"]);

  });

})();
