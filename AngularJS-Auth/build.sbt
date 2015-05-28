name := "PlayBasics-AngularJS-Auth"

libraryDependencies ++= Seq(
  cache,
  jdbc,
  evolutions,
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "org.webjars" % "requirejs" % "2.1.14-1",
  "org.webjars" % "angularjs" % "1.2.18"
)

pipelineStages := Seq(rjs)
