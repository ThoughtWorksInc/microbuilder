# For Haxe client

In order to call a RESTful JSON API from a Haxe application,
you need the following steps:

1. Create a Sbt project, which contains the MIDL definition of the RESTful JSON API
   that you will call in your JavaScript application.
   The project is also known as the API's SDK.
2. Publish the package of SDK to Haxelib or install it locally.
3. Add the dependency to the SDK into your client application build configuration.
4. In your client application, initilize the service defined in SDK and invoke methods of the service.

In this article,
we will create a command-line program that queries the public organization memberships for a specified Github user.
It will call [Github API](https://developer.github.com/v3/) via a Microbuilder SDK,
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

## Create the SDK

### Sbt configuration for SDK

Prepare an empty directory for the SDK project.
Then, create `project/plugins.sbt`
and add [sbt-microbuilder](https://github.com/ThoughtWorksInc/sbt-microbuilder) dependency into it.

    // project/plugins.sbt
    addSbtPlugin("com.thoughtworks.microbuilder" % "sbt-microbuilder" % "4.0.6")

There are multiple plugins in the sbt-microbuilder library.
`MicrobuilderCommon` plugin provides the settings for a Haxe SDK library.
The `MicrobuilderCommon` will be automatically enabled once you enable other plugins in sbt-microbuilder.

So you edit build.sbt, and add `enablePlugins(MicrobuilderJsSdk)` or `enablePlugins(MicrobuilderJavaSdk)` :

    // build.sbt
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

Now you can execute the following command to compile and package the SDK to a NPM package.

    sbt haxelibInstallDependencies haxe-js:packageNpm

## Publish the SDK to Haxelib

Before you publish the SDK to Haxelib,
you must register a user name for haxelib.
Run the following command for the registration

    haxelib register

Then, add some necessary settings in `build.sbt`, including:

* `haxelibReleaseNote`
* `haxelibContributors`
* `haxelibDependencies`
* `licenses`
* `haxelibDependencies`
* `haxelibSubmitUsername`

See this [repository](https://github.com/ThoughtWorksInc/github-sdk/tree/v1.0.10) for the complete code base.

Then, run the following command in your shell.

    sbt haxe:publish

### Setup a command-line program

Now, create your client-side application.

First, install `github-sdk` by executing the following command in shell:

    haxelib install github-sdk

Then you will need to install `hxssl`, because the Github API is HTTPS protocol.

    haxelib git hxssl https://github.com/tong/hxssl.git

Now, create a `build.hxml` and add the dependency to github-sdk.

    # Library dependencies
    -lib github-sdk
    -lib hxssl:git

    # Execute the `Main` class
    -x Main

This `build.hxml` contains flags for Haxe compiler.
When you run `haxe build.hxml`,
the Haxe compiler will run `Main.hx` as entry point of this program,
with library dependencies `github-sdk` and `hxssl`.

### The source file of the command-line program

Now you will create the source code for the command-lib program.
Create `Main.hx`, and edit it:

    import com.thoughtworks.microbuilder.tutorial.githubSdk.model.*;
    import com.thoughtworks.microbuilder.tutorial.githubSdk.proxy.*;
    import com.thoughtworks.microbuilder.core.*;

    class Main {

    	public static function main() {
    		// Initialize organization service in Github SDK
    		var organizationService = {
    			var urlPrefix = "https://api.github.com/";
    			var routeConfiguration = MicrobuilderRouteConfigurationFactory.routeConfiguration_com_thoughtworks_microbuilder_tutorial_githubSdk_rpc_IOrganizationService();
    			var outgoingJsonService = new MicrobuilderOutgoingJsonService(urlPrefix, routeConfiguration);
    			MicrobuilderOutgoingProxyFactory.outgoingProxy_com_thoughtworks_microbuilder_tutorial_githubSdk_rpc_IOrganizationService(outgoingJsonService);
    		}

    		// Read GITHUB_USERNAME environment variables
    		var username = Sys.environment()["GITHUB_USERNAME"];

    		// Send a request to Github API via organization service
    		organizationService.listUserOrganizations(username).start(
    			function(response):Void {
    				trace('$username belong to ${response.length} organizations: ${ [ for (organization in response) "\n * " + organization.login ].join("") }');
    			},
    			function(error):Void {
    				trace('Error: $error');
    			}
    		);
    	}

    }

`organizationService.listUserOrganizations` returns a [Future1](https://oss.sonatype.org/service/local/repositories/snapshots/archive/com/thoughtworks/microbuilder/json-stream-core/3.0.3-SNAPSHOT/json-stream-core-3.0.3-SNAPSHOT-javadoc.jar/!/jsonStream/rpc/Future1.html),
which pass the response data to the first function you passed to `start` method.

The example is complete now.

You can run it in shell and see its output:

    $ export GITHUB_USERNAME=Atry
    $ haxe build.hxml
    Main.hx:22: Atry belong to 3 organizations:
     * ThoughtWorksInc
     * ThoughtWorks-Xi-an-Scala-Camp
     * thoughtworkschina

You can find the entire example of command-line program at [organization-list-cli](https://github.com/ThoughtWorksInc/organization-list-cli).
