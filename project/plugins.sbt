lazy val plugins = project in file(".") dependsOn `sbt-microbuilder` dependsOn `sbt-haxe`

lazy val `sbt-microbuilder` = project dependsOn `sbt-haxe`

lazy val `sbt-haxe` = project

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.2")

addSbtPlugin("org.planet42" % "laika-sbt" % "0.5.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.5.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.2")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.2")
