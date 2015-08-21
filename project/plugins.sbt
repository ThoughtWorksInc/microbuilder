lazy val plugins = project in file(".") dependsOn `sbt-rest-rpc` dependsOn `sbt-release` dependsOn `sbt-haxe`

lazy val `sbt-rest-rpc` = project

lazy val `sbt-haxe` = project

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.8.1")


// Use a unstable version of sbt-release due to a bug in sbt-release 1.0.1(https://github.com/sbt/sbt-release/pull/122)
lazy val `sbt-release` = RootProject(uri("https://github.com/sbt/sbt-release.git#master"))
