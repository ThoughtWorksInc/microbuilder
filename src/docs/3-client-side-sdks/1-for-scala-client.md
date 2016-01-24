# For Scala client

In order to call a RESTful JSON API from a Scala application,
you need the following steps:

 1. Create a Sbt project, which contains the MIDL definition of the RESTful JSON API
    that you will call in your Scala application.
    The project is also known as the API's SDK.
 2. Publish the JAR package of SDK (optional).
 3. Add the dependency of SDK to your Scala application.
 4. In your Scala application, initilize the service defined in SDK and invoke methods of the service.

In this article,
we will create an application that queries the public organization memberships for a specified Github user.
The application is a Scala Play web project,
and will call [Github API](https://developer.github.com/v3/) via a Microbuilder SDK,
which is also created in this article.

## Requirement

Microbuilder requires [Haxe](http://haxe.org/) for code generation,
and [Sbt](http://www.scala-sbt.org/) to manage build pipeline.

You need download and install [Haxe 3.2.1](http://haxe.org/download/) and
[Sbt 0.13.9](http://www.scala-sbt.org/download.html),
then setup Haxelib with the folloiwing commands:

    haxelib setup ~/.haxelib.d
    haxelib install hxjava
    haxelib install dox
    haxelib install hxcs

## Create the SDK

### Sbt configuration for SDK

Prepare an empty directory for the SDK project.
Then, create `project/plugins.sbt`
and add [sbt-microbuilder](https://github.com/ThoughtWorksInc/sbt-microbuilder) dependency into it.

    // project/plugins.sbt
    addSbtPlugin("com.thoughtworks.microbuilder" % "sbt-microbuilder" % "3.0.1")

There are multiple plugins in the sbt-microbuilder library.
`MicrobuilderJavaSdk` is one of these plugins.
The plugin will add necessary settings for a SDK project for JVM target.

So you edit build.sbt, and add `enablePlugins(MicrobuilderJavaSdk)`:

    // build.sbt
    enablePlugins(MicrobuilderJavaSdk)

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
    import com.thoughtworks.microbuilder.tutorial.githubSdk.model.OrgnizationSummary;

    @:nativeGen
    interface IOrganizationService {

      @:route("GET", "/users/{username}/orgs")
      function listUserOrganizations(username:String):Future<Vector<OrgnizationSummary>>;

      // TODO: Other endpoints described at https://developer.github.com/v3/orgs/

    }

### MIDL JSON schema definition

The MIDL reference to a MIDL JSON schema `OrgnizationSummary`,
which should be defined under `com.thoughtworks.microbuilder.tutorial.githubSdk.model` package,
or `src/haxe/com/thoughtworks/microbuilder/tutorial/githubSdk/model` directory.
You can run `sbt jsonStreamModelModules::packageName` in shell to check that.

    // src/haxe/com/thoughtworks/microbuilder/tutorial/githubSdk/model/OrgnizationSummary.hx
    package com.thoughtworks.microbuilder.tutorial.githubSdk.model;

    @:final
    class OrgnizationSummary {

      public function new() {}

      public var login:String;

      public var id:Int;

      public var url:String;

      public var avatar_url:String;

      public var description:String;

    }

Now you can execute the following command to compile and package the SDK to a JAR.

    sbt haxelibInstallDependencies package

## Publish the SDK

See [Publishing](http://www.scala-sbt.org/0.13/docs/Publishing.html) in Sbt documentation for how to setup Sbt to publish your SDK.

See this [repository](https://github.com/ThoughtWorksInc/github-sdk/tree/v1.0.0) for the complete code base.

## Dependency settings for Client application

Now, create your client-side application.

(To be continued)

## Use SDK

(To be continued)
