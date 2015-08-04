lazy val plugins = project in file(".") dependsOn `sbt-rest-rpc`

lazy val `sbt-rest-rpc` = project

addSbtPlugin("com.qifun" % "sbt-haxe" % "1.3.0")


