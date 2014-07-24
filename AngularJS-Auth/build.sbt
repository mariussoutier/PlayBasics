name := "PlayBasics-AngularJS-Auth"

libraryDependencies ++= Seq(
  cache,
  jdbc,
  anorm,
  "org.webjars" % "requirejs" % "2.1.14-1",
  "org.webjars" % "angularjs" % "1.2.18"
)

pipelineStages := Seq(rjs)
