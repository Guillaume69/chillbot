name := """chillbot-srv"""

version := "1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"
resolvers += Resolver.jcenterRepo

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.12.6")

libraryDependencies += guice
libraryDependencies += cacheApi
libraryDependencies += ehcache
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "mysql" % "mysql-connector-java" % "6.0.6"
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",
)

libraryDependencies += "net.codingwell" %% "scala-guice" % "4.1.0"
libraryDependencies += "com.iheart" %% "ficus" % "1.4.1"

libraryDependencies += "ai.x" %% "play-json-extensions" % "0.10.0"

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "5.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.0"
)
