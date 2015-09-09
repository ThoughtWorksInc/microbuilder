organization := "com.thoughtworks"

name := "rest-rpc"

lazy val `auto-parser` = project

lazy val hamu = project

lazy val `json-stream` = project

lazy val `rest-rpc-core` = project dependsOn `auto-parser` dependsOn hamu dependsOn `json-stream`

lazy val `rest-rpc-play` = project dependsOn `rest-rpc-core`

lazy val `rest-rpc-sample` = project dependsOn `rest-rpc-play`
