lazy val plugins = project in file(".") dependsOn `sbt-microbuilder` dependsOn `sbt-release` dependsOn `sbt-haxe`

lazy val `sbt-microbuilder` = project dependsOn `sbt-haxe`

lazy val `sbt-haxe` = project

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.2")

addSbtPlugin("org.planet42" % "laika-sbt" % "0.5.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.5.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.2")

// Use a unstable version of sbt-release due to a bug in sbt-release 1.0.1(https://github.com/sbt/sbt-release/pull/122)
lazy val `sbt-release` = RootProject(uri("https://github.com/sbt/sbt-release.git#master"))
