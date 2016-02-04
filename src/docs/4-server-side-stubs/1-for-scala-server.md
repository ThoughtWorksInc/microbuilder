# For Scala server

Microbuilder generates stub implementations of a RESTful JSON server
for [Scala](https://www.scala-lang.org/) / [Play](https://www.playframework.com/) services,
once you provided MIDL definition for the API.

You can create your service in the following steps

1. Create a Sbt project, which contains the MIDL definition of the RESTful JSON API
   that you will implement in your Play application.
   The project is also known as the API's SDK.
2. Publish the JAR package of SDK (optional).
3. Add the dependency to the SDK into your Play application's `build.sbt`.
4. In your Play application, implement interfaces defined in SDK,
   and configure Play framework to use your interfaces.

In this article,
you will create a [BFF](https://www.thoughtworks.com/radar/techniques/bff-backend-for-frontends) service that queries the public organization memberships for a specified Github user,
and then strip out unnecessary data from the original response.
Finally the BFF service returns a compact data format of these organizations.

## Requirement

Microbuilder requires [Haxe](http://haxe.org/) for code generation,
and [Sbt](http://www.scala-sbt.org/) to manage build pipeline.

You need download and install [Haxe 3.2.1](http://haxe.org/download/) and
[Sbt 0.13.9](http://www.scala-sbt.org/download.html),
then setup Haxelib with the folloiwing commands:

    haxelib setup ~/.haxelib.d
    haxelib install hxjava
    haxelib install dox

## Create the BFF SDK

### Sbt configuration for the BFF SDK

Prepare an empty directory for the BFF SDK project.
Then, create `project/plugins.sbt`
and add [sbt-microbuilder](https://github.com/ThoughtWorksInc/sbt-microbuilder) dependency into it.

    // project/plugins.sbt
    addSbtPlugin("com.thoughtworks.microbuilder" % "sbt-microbuilder" % "3.0.2")

There are multiple plugins in the sbt-microbuilder library.
`MicrobuilderJavaSdk` is one of these plugins.
The plugin will add necessary settings for a SDK project for JVM target.

So you edit build.sbt, and add `enablePlugins(MicrobuilderJavaSdk)`:

    // build.sbt
    enablePlugins(MicrobuilderJavaSdk)

    organization := "com.thoughtworks.microbuilder.tutorial"

    name := "organization-bff-sdk"

### MIDL API definition

By default, your MIDL is under `src/haxe` directory,
and the package name of your MIDL is caculated from `organization` and `name` settings in your `build.sbt`.

The interface of MIDL API should be put under `com.thoughtworks.microbuilder.tutorial.organizationBffSdk.rpc` package,
or `src/haxe/com/thoughtworks/microbuilder/tutorial/organizationBffSdk/rpc` directory.
You can run `sbt jsonStreamServiceModules::packageName` in shell to check that.

Now you define [List user organizations](https://developer.github.com/v3/orgs/#list-user-organizations)
endpoint in a MIDL interface.

    // src/haxe/com/thoughtworks/microbuilder/tutorial/organizationBffSdk/rpc/IOrganizationService.hx
    package com.thoughtworks.microbuilder.tutorial.organizationBffSdk.rpc;

    import jsonStream.rpc.Future;
    import haxe.ds.Vector;
    import com.thoughtworks.microbuilder.tutorial.organizationBffSdk.model.BffOrganizationList;

    @:nativeGen
    interface IOrganizationBffService {

      @:route("GET", "users/{username}/orgs")
      function listUserOrganizations(username:String):Future<BffOrganizationList>;

      // TODO: Other endpoints described at https://developer.github.com/v3/orgs/

    }

### MIDL JSON schema definition

The MIDL reference to a MIDL JSON schema `BffOrganizationList`,
which should be defined under `com.thoughtworks.microbuilder.tutorial.organizationBffSdk.model` package,
or `src/haxe/com/thoughtworks/microbuilder/tutorial/organizationBffSdk/model` directory.
You can run `sbt jsonStreamModelModules::packageName` in shell to check that.

    // src/haxe/com/thoughtworks/microbuilder/tutorial/organizationBffSdk/model/BffOrganizationList.hx
    package com.thoughtworks.microbuilder.tutorial.organizationBffSdk.model;

    import haxe.ds.Vector;

    @:final
    class BffOrganization {

      public function new() {}

      public var name:String;

      public var iconUrl:String;

      public var description:String;

    }

    @:final
    class BffOrganizationList {

      public function new() {}

      public var name:String;

      public var organizations:Vector<BffOrganization>;

    }

Now you can execute the following command to compile and package the SDK to a JAR.

    sbt haxelibInstallDependencies package

## Publish the SDK

See [Publishing](http://www.scala-sbt.org/0.13/docs/Publishing.html) in Sbt documentation for how to setup Sbt to publish your SDK.

See this [repository](https://github.com/ThoughtWorksInc/organization-bff-sdk/tree/v1.0.0) for the complete code base.

## Dependency settings for the BFF application

Now, create your BFF application.

### Setup a Play project

First, create the project with the help of `sbt` or `activator`. You can see the documentation from [Play Framework web site](https://www.playframework.com/).

### Add SDK dependencies

Then, add the SDK dependencies to your `build.sbt`:

    // Dependency to the BFF SDK you just created.
    libraryDependencies += "com.thoughtworks.microbuilder.tutorial" %% "organization-bff-sdk" % "1.0.0"

    // Dependency to the Github SDK you created before.
    libraryDependencies += "com.thoughtworks.microbuilder.tutorial" %% "github-sdk" % "1.0.2"

    // Runtime library to integrate SDK into a Play application.
    libraryDependencies += "com.thoughtworks.microbuilder" %% "microbuilder-play" % "4.0.0"

    // A library that deals with asynchronous operation.
    libraryDependencies += "com.thoughtworks.each" %% "each" % "0.5.1"

Alternatively, You can embed the code base of BFF SDK as a child project of the Play project,
if you don't want to publish the BFF SDK.
See [Multi-project builds](http://www.scala-sbt.org/0.13/docs/Multi-Project.html) for more information.

Note that the BFF server will use server-side BFF SDK, and client-side Github SDK.
The latter SDK was created in [Client-side SDKs](../client-side-sdks/1-for-scala-client.html) chapter.

### Implement the interfaces defined in BFF SDK

Now you will implement the interfaces in BFF SDK.
Each method in an interface represents an endpoint of the server.

    // app/com/thoughtworks/microbuilder/tutorial/organizationBff/OrganizationBffService.scala
    package com.thoughtworks.microbuilder.tutorial.organizationBff

    import com.thoughtworks.microbuilder.tutorial.githubSdk.model.OrganizationSummary
    import com.thoughtworks.microbuilder.tutorial.githubSdk.rpc.IOrganizationService
    import com.thoughtworks.microbuilder.tutorial.organizationBffSdk.model.{BffOrganization, BffOrganizationList}
    import com.thoughtworks.microbuilder.tutorial.organizationBffSdk.rpc.IOrganizationBffService
    import scalaz.std.scalaFuture._

    import scala.concurrent.{ExecutionContext, Future}
    import com.thoughtworks.each.Monadic._
    import com.thoughtworks.microbuilder.play.Implicits._

    class OrganizationBffService(organizationService: IOrganizationService)(implicit ec: ExecutionContext) extends IOrganizationBffService {
      override def listUserOrganizations(username: String) = throwableMonadic[Future] {
        val response = new BffOrganizationList
        response.name = username
        val githubOrganizationsFuture: Future[Array[OrganizationSummary]] = organizationService.listUserOrganizations(username)
        val githubOrganizations = githubOrganizationsFuture.each
        response.organizations = for {
          githubOrganization &lt;- githubOrganizations
        } yield {
          val bffOrganization = new BffOrganization
          bffOrganization.description = githubOrganization.description
          bffOrganization.iconUrl = githubOrganization.avatar_url
          bffOrganization.name = githubOrganization.login
          bffOrganization
        }
        response
      }
    }

In this example,
the BFF server send a request to Github API for organization list of a user,
then it fill create a `BffOrganizationList`,
and fill it with the response from Github API.

### Configure Play framework to use the service implementation

Microbuilder provides a `RpcController`,
which forwards HTTP request to your service implementation.

    # conf/routes
    GET        /*relatedUrl        com.thoughtworks.microbuilder.play.RpcController.rpc(relatedUrl:String)

Then, create the loader for this Play application.

    package com.thoughtworks.microbuilder.tutorial.organizationBff

    import com.thoughtworks.microbuilder.play.{RpcEntry, PlayOutgoingJsonService, RpcController}
    import com.thoughtworks.microbuilder.tutorial.githubSdk.proxy.MicrobuilderOutgoingProxyFactory._
    import com.thoughtworks.microbuilder.tutorial.githubSdk.proxy.MicrobuilderRouteConfigurationFactory._
    import com.thoughtworks.microbuilder.tutorial.organizationBffSdk.proxy.MicrobuilderIncomingProxyFactory._
    import com.thoughtworks.microbuilder.tutorial.organizationBffSdk.proxy.MicrobuilderRouteConfigurationFactory._
    import com.thoughtworks.microbuilder.tutorial.githubSdk.rpc.IOrganizationService
    import com.thoughtworks.microbuilder.tutorial.organizationBffSdk.rpc.IOrganizationBffService
    import play.api.libs.ws.ning.NingWSComponents
    import play.api.{BuiltInComponentsFromContext, Application, ApplicationLoader}
    import play.api.ApplicationLoader.Context
    import router.Routes

    class Loader extends ApplicationLoader {
      override def load(context: Context): Application = {

      val components = new BuiltInComponentsFromContext(context) with NingWSComponents {
        implicit def executionContext = actorSystem.dispatcher

        lazy val organizationService = PlayOutgoingJsonService.newProxy[IOrganizationService]("https://api.github.com/", wsApi)

        lazy val bffService = new OrganizationBffService(organizationService)

        lazy val rpcController = new RpcController(Seq(RpcEntry.implementedBy[IOrganizationBffService](bffService)))

        override lazy val router = new Routes(httpErrorHandler, rpcController)
      }

      components.application
      }
    }

The `routes` you just created requires a `RpcController` instance.
So you created the `RpcController` instance,
and set the underlying service implementation `OrganizationBffService` for the `RpcController` instance.

And set-up the entry point of the application in `conf/appication.conf`:

    play.application.loader = com.thoughtworks.microbuilder.tutorial.organizationBff.Loader

Now you built the entire application.
You can run it from Sbt

```
sbt run
```

Then visit http://localhost:9000/users/your-user-name/orgs .
You will see a JSON of your Github organization list.
、
You can find the entire example of server-side BFF at [organization-bff](https://github.com/ThoughtWorksInc/organization-bff).
