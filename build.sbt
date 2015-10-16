import LaikaKeys._
import GhPagesKeys._

organization := "com.thoughtworks"

name := "rest-rpc"

lazy val hamu = project

lazy val `auto-parser` = project dependsOn hamu

lazy val `json-stream` = project

lazy val `rest-rpc-core` = project dependsOn `auto-parser` dependsOn hamu dependsOn `json-stream`

lazy val `rest-rpc-play` = project dependsOn `rest-rpc-core`

lazy val `microbuilder-sample` = project dependsOn `rest-rpc-play`


ghpages.settings

git.remoteRepo := "git@github.com:ThoughtWorksInc/rest-rpc.git"

git.gitCurrentBranch := "master"

LaikaPlugin.defaults

mappings in synchLocal := (mappings in LaikaKeys.site in Laika).value
