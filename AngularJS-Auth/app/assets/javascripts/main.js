(function() {
  "use strict";

  requirejs.config({
    shim: {
      'jsRoutes': {
        deps: [],
        // it's not a RequireJS module, so we have to tell it what var is returned
        exports: 'jsRoutes'
      },
      'angular': {
        deps: ['jquery'],
        exports: 'angular'
      },
      'angular-cookies': ['angular']
    },
    paths: {
      'requirejs': ['../lib/requirejs/require'],
      'jquery': ['../lib/jquery/jquery'],
      'angular': ['../lib/angularjs/angular'],
      'angular-cookies': ['../lib/angularjs/angular-cookies'],
      'jsRoutes': ['/ng/jsroutes']
    }
  });

  require(["./controllers/login", "angular", "angular-cookies"],
    function(login, angular) {

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
