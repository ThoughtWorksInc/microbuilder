#!/bin/bash

source ./secret/git_config.sh
rm -rf $HOME/.sbt/ghpages
sbt "++ $TRAVIS_SCALA_VERSION ghpagesPushSite"
