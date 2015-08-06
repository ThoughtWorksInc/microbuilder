organization := "com.thoughtworks"

name := "rest-rpc"

//lazy val root = project in file(".")

lazy val `rest-rpc-core` = project

lazy val `rest-rpc-play` = project dependsOn `rest-rpc-core`

lazy val `rest-rpc-sample` = project dependsOn `rest-rpc-play`