organization := "com.thoughtworks"

name := "rest-rpc"

lazy val `auto-parser` = RootProject(uri("git://github.com/Atry/auto-parser.git"))

lazy val `rest-rpc-core` = project dependsOn `auto-parser`

lazy val `rest-rpc-play` = project dependsOn `rest-rpc-core`

lazy val `rest-rpc-sample` = project dependsOn `rest-rpc-play`