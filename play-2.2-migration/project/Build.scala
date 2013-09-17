import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play22migration"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // jdbc
    // anorm
    // cache
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )

}
