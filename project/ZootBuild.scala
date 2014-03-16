import sbt._
import Keys._

object ZootBuild extends Build {

    val scalaReflect = "org.scala-lang" % "scala-reflect" % "2.10.3"
    val scalaCompiler = "org.scala-lang" % "scala-compiler" % "2.10.3"
    val cglib = "cglib" % "cglib-nodep" % "3.1"
    val smirror = "net.fwbrasil" %% "smirror" % "0.7-SNAPSHOT"
    val jacksonScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.3.0"
    val scalaTest = "org.scalatest" %% "scalatest" % "2.0" % "test"
    val mockito = "org.mockito" % "mockito-all" % "1.9.5"

    lazy val zoot =
        Project(
            id = "zoot",
            base = file("."),
            aggregate = Seq(zootCore, zootSpray, zootFinagle),
            settings = commonSettings)

    lazy val zootCore =
        Project(
            id = "zoot-core",
            base = file("zoot-core"),
            settings = commonSettings ++ Seq(
                libraryDependencies ++=
                    Seq(scalaReflect, scalaCompiler, cglib, smirror, 
                        jacksonScala, scalaTest, mockito)))

    val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.3.0"
    val sprayCan = "io.spray" % "spray-can" % "1.3.0" withSources()

    lazy val zootSpray =
        Project(
            id = "zoot-spray",
            base = file("zoot-spray"),
            settings = commonSettings ++ Seq(
                libraryDependencies ++=
                    Seq(sprayCan, akkaActor, scalaTest)),
            dependencies = Seq(zootCore))

    val finagleHttp = "com.twitter" %% "finagle-http" % "6.10.0"  exclude("org.scala-tools.testing", "specs_2.10") exclude("org.mockito", "mockito-all") exclude("junit", "junit")  withSources()
    val finagleCore = "com.twitter" %% "finagle-core" % "6.10.0"  exclude("org.scala-tools.testing", "specs_2.10") exclude("org.mockito", "mockito-all") exclude("junit", "junit")  withSources()

    lazy val zootFinagle =
        Project(
            id = "zoot-finagle",
            base = file("zoot-finagle"),
            settings = commonSettings ++ Seq(
                libraryDependencies ++=
                    Seq(finagleHttp, finagleCore, scalaTest)),
            dependencies = Seq(zootCore))

    val customResolvers = Seq(
        "spray repo" at "http://repo.spray.io/",
        "Maven" at "http://repo1.maven.org/maven2/",
        "Typesafe" at "http://repo.typesafe.com/typesafe/releases",
        "Local Maven Repository" at "file://" + Path.userHome + "/.m2/repository",
        "fwbrasil.net" at "http://fwbrasil.net/maven/",
        "Vaadin Addons" at "http://maven.vaadin.com/vaadin-addons")

    def commonSettings =
        Defaults.defaultSettings ++ ScoverageSbtPlugin.instrumentSettings ++ Seq(
            organization := "net.fwbrasil",
            version := "0.4-SNAPSHOT",
            publishMavenStyle := true,
            scalaVersion := "2.10.3",
            parallelExecution in Test := false,
            parallelExecution in ScoverageSbtPlugin.scoverageTest := false,
            // publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository"))), 
            publishTo := Option(Resolver.ssh("fwbrasil.net repo", "fwbrasil.net", 8080) as ("maven") withPermissions ("0644")),
            resolvers ++= customResolvers,
            compileOrder := CompileOrder.JavaThenScala)
}
