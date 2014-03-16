resolvers += Classpaths.typesafeResolver

resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.2.0")

addSbtPlugin("com.sksamuel.scoverage" %% "sbt-scoverage" % "0.95.7")