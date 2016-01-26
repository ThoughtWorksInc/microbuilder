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

Now you can execute the following command to compile and package the SDK to a JAR.

    sbt haxelibInstallDependencies package

## Publish the SDK

See [Publishing](http://www.scala-sbt.org/0.13/docs/Publishing.html) in Sbt documentation for how to setup Sbt to publish your SDK.

See this [repository](https://github.com/ThoughtWorksInc/github-sdk/tree/v1.0.2) for the complete code base.

## Dependency settings for Client application

Now, create your client-side application.

### Setup a Play project

Fist, create the project with the help of `sbt` or `activator`. You can see the documentation from [Play Framework web site](https://www.playframework.com/).

### Add SDK dependencies

Then, add the SDK dependencies to your `build.sbt`:

    // Dependency to the Github SDK we just created.
    libraryDependencies += "com.thoughtworks.microbuilder.tutorial" %% "github-sdk" % "1.0.2"

    // Runtime library to integrate SDK into a Play application.
    libraryDependencies += "com.thoughtworks.microbuilder" %% "microbuilder-play" % "3.1.0"

    // A library that deals with asynchronous operation.
    libraryDependencies += "com.thoughtworks.each" %% "each" % "0.5.1"

### Use the SDK

After the SDK dependencies are added, we can create a controller which use `IOrganizationService` sending RESTful requests.

    package com.thoughtworks.microbuilder.tutorial.organizationList.controllers

    import play.api.mvc.{Action, Controller}

    import scala.concurrent.{ExecutionContext, Future}

    import scalaz.std.scalaFuture._
    import com.thoughtworks.each.Monadic._

    import com.thoughtworks.microbuilder.play.Implicits._

    import com.thoughtworks.microbuilder.tutorial.githubSdk.model.OrganizationSummary
    import com.thoughtworks.microbuilder.tutorial.githubSdk.rpc.IOrganizationService
    import com.thoughtworks.microbuilder.tutorial.organizationList.views.html.renderOrganizationList


    class OrganizationListController(organizationService: IOrganizationService)(implicit ec: ExecutionContext) extends Controller {

      def showOrganizationList(username: String) = Action.async(throwableMonadic[Future] {
        val future: Future[Array[OrganizationSummary]] = organizationService.listUserOrganizations(username)
        Ok(renderOrganizationList(username, future.each))
      })

    }

In this example, you convert result of `listUserOrganizations` to a asynchronous `Future`,
then use [Scalaz](https://scalaz.github.io/scalaz) and [Each](https://github.com/ThoughtWorksInc/each) handling the future.

### Other Play configurations

In order to use the `OrganizationListController`,
add corresponding Play's routes configuration for it.

    # conf/routes
    GET        /users/:username        com.thoughtworks.microbuilder.tutorial.organizationList.controllers.OrganizationListController.showOrganizationList(username:String)

Then, create the Twirl template `renderOrganizationList` used by `OrganizationListController`

    @import com.thoughtworks.microbuilder.tutorial.githubSdk.model.OrganizationSummary
    @(username: String, organizations: Array[OrganizationSummary])
    <html>
      <body>
        <h1>@username's organizations</h1>
        <ul>
          @for(organization <- organizations) {
            <li>
              <img src="@organization.avatar_url" width="20" height="20" title="@organization.description"/>
              @organization.login
            </li>
          }
        </ul>
      </body>
    </html>

This template renders data got from Github API.

### Initialize the SDK

As we defined before,
the `organizationService:IOrganizationService` is a parameter of `OrganizationListController`,
which should be initialized in the Play framework's [application entry point](https://www.playframework.com/documentation/2.4.x/ScalaCompileTimeDependencyInjection#Application-entry-point).

    package com.thoughtworks.microbuilder.tutorial.organizationList

    import com.thoughtworks.microbuilder.play.PlayOutgoingJsonService
    import com.thoughtworks.microbuilder.tutorial.githubSdk.proxy.MicrobuilderOutgoingProxyFactory._
    import com.thoughtworks.microbuilder.tutorial.githubSdk.proxy.MicrobuilderRouteConfigurationFactory._
    import com.thoughtworks.microbuilder.tutorial.githubSdk.rpc.IOrganizationService
    import com.thoughtworks.microbuilder.tutorial.organizationList.controllers.OrganizationListController
    import play.api.libs.ws.ning.NingWSComponents
    import play.api.{BuiltInComponentsFromContext, Application, ApplicationLoader}
    import play.api.ApplicationLoader.Context
    import router.Routes

    class Loader extends ApplicationLoader {
      override def load(context: Context): Application = {

        val components = new BuiltInComponentsFromContext(context) with NingWSComponents {
          implicit def executionContext = actorSystem.dispatcher

          lazy val organizationService = PlayOutgoingJsonService.newProxy[IOrganizationService]("https://api.github.com/", wsApi)

          override lazy val router = new Routes(httpErrorHandler, new OrganizationListController(organizationService)(actorSystem.dispatcher))
        }

        components.application
      }
    }

Note that `newProxy` method will look for implcit stubs that generated from MIDL. They are `com.thoughtworks.microbuilder.tutorial.githubSdk.proxy.MicrobuilderOutgoingProxyFactory` and
`com.thoughtworks.microbuilder.tutorial.githubSdk.proxy.MicrobuilderRouteConfigurationFactory`.
You must import `MicrobuilderOutgoingProxyFactory._` and `MicrobuilderRouteConfigurationFactory._` to enable those implicit values generated in the two classes.
