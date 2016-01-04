Overview
====================

Microbuilder is a framework to build microservice. It just need three steps to finish a service:

* Define Models and Interfaces
* Involve Microbuilder
* Compile and Invoke in Your Code

Step1: Define Models and Interfaces
---------------------

* 1. src/haxe/model: define your model interact with callee
* 2. src/haxe/rpc: define a local method and relate it to the route

Step2: Involve Microbuilder
---------------------
* 1. add sbt plugin to plugins.sbt

```addSbtPlugin("com.thoughtworks" % "sbt-microbuilder" % "+")```

* 2. enable sbt plugin in build.sbt

```enablePlugins(MicrobuilderPlay)```


Step3: Compile and Invoke in Your Code
---------------------
* compile your model and interface

```sbt compile```

* write your logic code based on interface
