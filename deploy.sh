#!/bin/bash

sbt "++ $TRAVIS_SCALA_VERSION ghpagesPushSite"
