# Haxedoc

When you define some RESTful JSON API in MIDL,
you can generate a API reference documentation for the API.

## Requirement

Microbuilder requires [Haxe](http://haxe.org/) for documentation generation,
and [Sbt](http://www.scala-sbt.org/) to manage build pipeline.

You need download and install [Haxe 3.2.1](http://haxe.org/download/) and
[Sbt 0.13.9](http://www.scala-sbt.org/download.html),
then setup Haxelib with the folloiwing commands:

    haxelib setup ~/.haxelib.d
    haxelib install hxjava
    haxelib install dox

## Create the SDK

### Sbt configuration for SDK

Prepare an empty directory for the SDK project.
Then, create `project/plugins.sbt`
and add [sbt-microbuilder](https://github.com/ThoughtWorksInc/sbt-microbuilder) dependency into it.

    // project/plugins.sbt
    addSbtPlugin("com.thoughtworks.microbuilder" % "sbt-microbuilder" % "3.0.1")

There are multiple plugins in the sbt-microbuilder library for different targets or platforms.

You may want to add `enablePlugins` for some of these plugins :

    // build.sbt
    enablePlugins(MicrobuilderJavaSdk)

    enablePlugins(MicrobuilderJsSdk)

    organization := "com.thoughtworks.microbuilder.tutorial"

    name := "github-sdk"

### MIDL API definition

By default, your MIDL is under `src/haxe` directory,
and the package name of your MIDL is caculated from `organization` and `name` settings in your `build.sbt`.

The interface of MIDL API should be put under `com.thoughtworks.microbuilder.tutorial.githubSdk.rpc` package,
or `src/haxe/com/thoughtworks/microbuilder/tutorial/githubSdk/rpc` directory.
You can run `sbt jsonStreamServiceModules::packageName` in shell to check that.

Now you define [List user organizations](https://developer.github.com/v3/orgs/#list-user-organizations)
endpoint in a MIDL interface.

    // src/haxe/com/thoughtworks/microbuilder/tutorial/githubSdk/rpc/IOrganizationService.hx
    package com.thoughtworks.microbuilder.tutorial.githubSdk.rpc;

    import jsonStream.rpc.Future;
    import haxe.ds.Vector;
    import com.thoughtworks.microbuilder.tutorial.githubSdk.model.OrganizationSummary;

    @:nativeGen
    interface IOrganizationService {

      @:route("GET", "users/{username}/orgs")
      function listUserOrganizations(username:String):Future<Vector<OrganizationSummary>>;

      // TODO: Other endpoints described at https://developer.github.com/v3/orgs/

    }

### MIDL JSON schema definition

The MIDL reference to a MIDL JSON schema `OrganizationSummary`,
which should be defined under `com.thoughtworks.microbuilder.tutorial.githubSdk.model` package,
or `src/haxe/com/thoughtworks/microbuilder/tutorial/githubSdk/model` directory.
You can run `sbt jsonStreamModelModules::packageName` in shell to check that.

    // src/haxe/com/thoughtworks/microbuilder/tutorial/githubSdk/model/OrganizationSummary.hx
    package com.thoughtworks.microbuilder.tutorial.githubSdk.model;

    @:final
    class OrganizationSummary {

      public function new() {}

      public var login:String;

      public var id:Int;

      public var url:String;

      public var avatar_url:String;

      public var description:String;

    }

### Generate Haxedoc

Now you can execute the following command to generate Haxedoc

    sbt haxelibInstallDependencies haxe:doc

Navigate to `target/scala-2.10/compile-dox/index.html` and you will the API reference.



See this [repository](https://github.com/ThoughtWorksInc/github-sdk/tree/v1.0.3) for the complete code base.
