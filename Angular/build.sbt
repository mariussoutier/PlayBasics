// For the full build, check the root project
// This project is also reused by AngularJS-Auth

name := "PlayBasics-Angular"

libraryDependencies ++= Seq(
  "org.webjars" % "requirejs" % "2.1.14-1",
  "org.webjars" % "jquery" % "1.11.1",
  "org.webjars" % "angularjs" % "1.2.18"
)

// Run r.js (RequireJS optimizer) when building the app for production
pipelineStages := Seq(rjs)

// The r.js optimizer won't find jsRoutes so we must tell it to ignore it
RjsKeys.paths += ("jsRoutes" -> ("/jsroutes" -> "empty:"))

// Alternative to setting the router in the conf file:
//PlayKeys.devSettings += ("application.router", "ng.Routes")
