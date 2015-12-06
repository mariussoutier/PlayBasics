name := "PlayBasics-DependencyInjection"

lazy val Shared = project
lazy val CompileTime = project.enablePlugins(PlayScala).dependsOn(Shared)
lazy val Jsr330 = project.enablePlugins(PlayScala).dependsOn(Shared)

libraryDependencies in ThisBuild ++= Seq(
  ws, filters
)
