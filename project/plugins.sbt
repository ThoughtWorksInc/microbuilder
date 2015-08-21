lazy val plugins = project in file(".") dependsOn `sbt-rest-rpc`

lazy val `sbt-rest-rpc` = project

addSbtPlugin("com.qifun" % "sbt-haxe" % "1.3.0")

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1")


