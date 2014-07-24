// -- root build file --
// This lays out a project with several Play apps that can have dependencies on one another.
// This is in contrast to the modularized layout presented in Play's application, where there's a single running app.
// You can combine both methods however, i.e. split each running app into modules.

name := "PlayBasics"

// -- Build-wide settings --
// Apply to all sub-projects; avoids having to reference Common settings everywhere

organization in ThisBuild := "com.mariussoutier.example"

version in ThisBuild := "2.3.2"

scalaVersion in ThisBuild := "2.11.1"

// -- Sub-projects --

// `project` is a macro that reads the name of the val and finds a project with that name
// PlayScala is an sbt auto-plugin; when it is enabled, it automatically enables the plugin it depends on,
// like SbtWeb and Twirl (the templating engine)
lazy val Angular = project.enablePlugins(PlayScala)

// If the project has a name that cannot be reflected by the val's name, you use the normal way via project
// Another commonly seen way of formulating this is (project in file("AngularJS-Auth"))
lazy val AngularAuth = project.in(file("AngularJS-Auth"))
  .dependsOn(Angular)
  .enablePlugins(PlayScala)

lazy val AsyncScala = project.enablePlugins(PlayScala)

// RoutesJava is left out because it's still on Play 2.0.4
lazy val RoutesScala = project.enablePlugins(PlayScala)

lazy val ActionComposition = project.enablePlugins(PlayScala)

// The root project,
// It is defined automatically, including the aggregation part; you'd only declare it to enable plugins on it
//lazy val root = project.in(file(".")).aggregate(Angular, AngularAuth, AsyncScala, RoutesScala).enablePlugins(PlayScala)
