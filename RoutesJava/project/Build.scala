import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "PlayBasics-RoutesJ"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Scala Lang Binder
      routesImport += "extensions.Binders._"
      // Java Lang Binder
      // routesImport += "extensions.LangPathWrapper",
      // routesImport += "extensions.LangQueryWrapper"
    )

}
