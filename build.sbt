organization := "com.thoughtworks"

name := "rest-rpc"

lazy val hamu = project

lazy val `auto-parser` = project dependsOn hamu

lazy val `json-stream` = project

lazy val `rest-rpc-core` = project dependsOn `auto-parser` dependsOn hamu dependsOn `json-stream`

lazy val `rest-rpc-play` = project dependsOn `rest-rpc-core`

lazy val `rest-rpc-sample` = project dependsOn `rest-rpc-play`

site.settings

site.includeScaladoc()

ghpages.settings

git.remoteRepo := "git@github.com:ThoughtWorksInc/rest-rpc.git"

git.gitCurrentBranch := "master"
