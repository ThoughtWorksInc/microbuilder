organization := "com.thoughtworks"

name := "rest-rpc"

lazy val `rest-rpc-core` = project

lazy val `rest-rpc-play` = project dependsOn `rest-rpc-core`

lazy val `rest-rpc-sample` = project dependsOn `rest-rpc-play`

site.settings

site.includeScaladoc()

ghpages.settings

git.remoteRepo := "git@github.com:ThoughtWorksInc/rest-rpc.git"

git.gitCurrentBranch := "master"