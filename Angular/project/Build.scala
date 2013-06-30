import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "Angular"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "org.webjars" % "angularjs" % "1.0.7",
    "org.webjars" % "requirejs" % "2.1.1",
    "org.webjars" % "webjars-play" % "2.1.0-1"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )

}
