ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.16"

val AkkaVersion = "2.6.21"

resolvers += ("custome1" at "http://4thline.org/m2").withAllowInsecureProtocol(true)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

lazy val root = (project in file("."))
  .settings(
    name := "ChatSystem",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-remote" % AkkaVersion,
      "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.fourthline.cling" % "cling-core" % "2.1.2",
      "org.fourthline.cling" % "cling-support" % "2.1.2",
      "org.scalafx" %% "scalafx" % "8.0.192-R14",
      "org.scalafx" %% "scalafxml-core-sfx8" % "0.5"
    )
  )
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
