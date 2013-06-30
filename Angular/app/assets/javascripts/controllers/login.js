define(["angular"], function(angular) {
  "use strict";

  return {
    LoginCtrl: function($scope, playRoutes) {
      $scope.credentials = {};
      $scope.user = {};
      $scope.loggedIn = false;

      $scope.login = function(credentials) {
        playRoutes.controllers.Application.login().post(credentials).then(function(response) {
          console.log("Received Token: " + response.data.token);
          return playRoutes.controllers.Users.user(3).get(); // return promise so we can chain easily
        }).then(function(response) {
          // Promise of Users.user(3).get() is resolved here
          $scope.loggedIn = true;
          $scope.user = response.data;
        });
      };
    }
  };

});
