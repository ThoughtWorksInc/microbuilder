# For other language client

In order to call a RESTful JSON API from an application in any other language,
you need the following steps:

1. Create a Sbt project, which contains the MIDL definition of the RESTful JSON API
   that you will call in your JavaScript application.
   The project is also known as the API's SDK.
2. Compile or publish the package of SDK.
3. Add the dependency to the SDK into your client application build configuration.
4. In your client application, initilize the service defined in SDK and invoke methods of the service.

In this article,
we will create a Flash that queries the public organization memberships for a specified Github user.
It will call [Github API](https://developer.github.com/v3/) via a Microbuilder SDK,
which is also created in this article.

You can create any other targets that Haxe supports in the way.

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
    addSbtPlugin("com.thoughtworks.microbuilder" % "sbt-microbuilder" % "5.0.4")

There are multiple plugins in the sbt-microbuilder library.
You will add `MicrobuilderCommon` plugin,
which adds necessary settings for a SDK project for any target platform .

Also you may want to add `HaxeFlashPlugin` plugin to enable complilation from Haxe to SWF file.

If you want to create SDK for other platform, for example, C#,
then you can add `enablePlugins(HaxeCSharpPlugin)` to enable complilation from Haxe to C# target.

So you edit `build.sbt`, and add the following settings:


    // build.sbt
    enablePlugins(MicrobuilderCommon)

    enablePlugins(HaxeFlashPlugin)

    organization := "com.thoughtworks.microbuilder.tutorial"

    name := "github-sdk"

    isLibrary := true

    libraryDependencies ++= microbuilderHaxeDependencies(Flash)

If you are building other target than Flash, for example,
you can specify `libraryDependencies ++= microbuilderHaxeDependencies(CSharp)` for that target.

To enable build for all platforms that Haxe supports,
you can create a `for` / `yield` expression to generate the settings for all the platforms.

    for {
      c <- AllHaxeConfigurations
    } yield {
      libraryDependencies ++= microbuilderHaxeDependencies(c)
    }

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

See this [repository](https://github.com/ThoughtWorksInc/github-sdk/) for the complete code base.

## Build SWC libraries

### github-sdk.swc

Now you can execute the following command to compile the SDK to a SWC library.

    sbt haxelibInstallDependencies flash:haxe

You will see the library `target/src_managed/flash/github-sdk.swc` was created.

### microbuilder-core.swc

You will also need Microbuilder's run-time library for Flash.
The source code of the library is at https://github.com/ThoughtWorksInc/microbuilder-core .
There are only binary releases of the run-time for JavaScript and Java/Scala platform,
you need to build the library for Flash from source.

So you execute the following shell commands:

    git clone https://github.com/ThoughtWorksInc/microbuilder-core.git
    cd microbuilder-core
    sbt haxelibInstallDependencies flash:haxe

You will see the library `target/src_managed/flash/microbuilder-core.swc` was created.


## Create the Flash application

Now, create your Flash application.

### Prepare SWC Libraries

First, create an empty project directory.
Then, create a `lib` directory and copy `microbuilder-core.swc` and `github-sdk.swc` you previously built to `lib` directory.

### Edit source file

You will create a simple Flex application, the source file is `src/Main.mxml`:

    <?xml version="1.0" encoding="utf-8"?>
    <mx:Application
      xmlns:fx="http://ns.adobe.com/mxml/2009"
      xmlns:mx="library://ns.adobe.com/flex/mx"
      xmlns:s="library://ns.adobe.com/flex/spark"
      implements="jsonStream.rpc.ICompleteHandler1">
      <fx:Script><![CDATA[
        import com.thoughtworks.microbuilder.tutorial.githubSdk.model.*;
        import com.thoughtworks.microbuilder.tutorial.githubSdk.rpc.*;
        import com.thoughtworks.microbuilder.tutorial.githubSdk.proxy.*;
        import com.thoughtworks.microbuilder.core.*;
        import mx.controls.*;

        private static const URL_PRIFIX:String = "https://api.github.com/";

        // Initialize organization service in Github SDK
        private const routeConfiguration:IRouteConfiguration = MicrobuilderRouteConfigurationFactory.routeConfiguration_com_thoughtworks_microbuilder_tutorial_githubSdk_rpc_IOrganizationService();
        private const outgoingJsonService:MicrobuilderOutgoingJsonService = new MicrobuilderOutgoingJsonService(URL_PRIFIX, routeConfiguration);
        private const organizationService:IOrganizationService = MicrobuilderOutgoingProxyFactory.outgoingProxy_com_thoughtworks_microbuilder_tutorial_githubSdk_rpc_IOrganizationService(outgoingJsonService);



        public function onSuccess(response:Object):void {
          const organizations:Vector.<OrganizationSummary> = Vector.<OrganizationSummary>(response);
          const stringBuilder:Array = [];
          stringBuilder.push(username.text, " belong to ", organizations.length, " organizations:");
          for each(var organization:OrganizationSummary in organizations) {
            stringBuilder.push("\n * ", organization.login);
          }
          Alert.show(
            stringBuilder.join(""),
            "Result",
            Alert.OK);
        }

        public function onFailure(error:*):void {
          Alert.show(
            error,
            "Error",
            Alert.OK);
        }
      ]]></fx:Script>
      <mx:TextInput id="username"/>
      <mx:Button label="Query">
        <mx:click><![CDATA[
          organizationService.listUserOrganizations(username.text).start(this);
        ]]></mx:click>
      </mx:Button>
    </mx:Application>

In this example, you initialized a `organizationService` in `fx:Script`,
then call the RESTful API `listUserOrganizations` when the Query Button is clicked.

Note the `Main` instance `this` is passed to `listUserOrganizations` as response handler.
When the response is received, `onSuccess` will be triggered and a message box will be shown.

### Compile the Flash application

The example is complete now.
You will create `flex-config.xml` for the compilation options for Flex compiler.

    <?xml version="1.0"?>
    <flex-config xmlns="http://www.adobe.com/2006/flex-config">
    	<file-specs>
    		<path-element>src/Main.mxml</path-element>
    	</file-specs>
    	<output>bin/Main.swf</output>
    	<compiler>
    		<library-path append="true">
    			<path-element>lib/github-sdk.swc</path-element>
    			<path-element>lib/microbuilder-core.swc</path-element>
    		</library-path>
    	</compiler>
    </flex-config>

You can build the output Flex application `bin/Main.swf` by executing the following command:

    mxmlc -load-config+=flex-config.xml

Open `bin/Main.swf` in a browser,
input you Github username in the text box,
and click the Query button,
then you will see your Github organization list.

You can find the entire example of client-side application at [organization-list-flash](https://github.com/ThoughtWorksInc/organization-list-flash).
