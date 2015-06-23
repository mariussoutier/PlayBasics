name := "PlayBasics-DependencyInjection"

lazy val Jsr330 = project.enablePlugins(PlayScala)

libraryDependencies in ThisBuild ++= Seq(
  ws
)
