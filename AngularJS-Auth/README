AngularJS + Play + Security
===========================

Instructions
------------

* Install play 2.1.x
 * Download from www.playframework.org
 * Or on a Mac use `brew install play`
* `cd` into the directory
* Execute `play update ~run`
* Head to [http://localhost:9000](http://localhost:9000)
* Apply the evolutions (it's only an H2 in-memory database)


Playing around
--------------
* This demo offers logging it, logging out and executing some action that needs authorized access,
  in this case a simple ping; the ping is also used to check the token's validity by the client
* The token is only valid for two minutes, so you can see what happens if you ping after that
  (this can be changed by setting `cache.expiration` in `conf/application.conf`)
* Take a look at the various components
 * `app/assets/javascripts` - LoginCtrl and app.js
 * `app/controllers` - the server side controllers written in Scala/Play
  * `Application.scala` - handles token, login, logout
  * `Users.scala` - retrieves the user data
 * `app/models` - the data model, only `User.scala`, implemented using a simple case class, with
   accompanying JSON transformation and SQL queries
* `app/views/index.scala.html` - HTML template
