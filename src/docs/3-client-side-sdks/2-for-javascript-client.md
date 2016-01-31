# For JavaScript client

In order to call a RESTful JSON API from a JavaScript application,
you need the following steps:

 1. Create a Sbt project, which contains the MIDL definition of the RESTful JSON API
    that you will call in your JavaScript application.
    The project is also known as the API's SDK.
 2. Publish the NPM package of SDK.
 3. Add the dependency to the SDK into your JavaScript application's `package.json`.
 4. In your JavaScript application, initilize the service defined in SDK and invoke methods of the service.

In this article,
we will create an application that queries the public organization memberships for a specified Github user.
The application is a static web project,
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
`MicrobuilderJsSdk` is one of these plugins.
The plugin will add necessary settings for a SDK project for JavaScript target.

So you edit build.sbt, and add `enablePlugins(MicrobuilderJsSdk)`:

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

## Publish the SDK

See [Publishing](http://www.scala-sbt.org/0.13/docs/Publishing.html) in Sbt documentation for how to setup Sbt to publish your SDK.

See this [repository](https://github.com/ThoughtWorksInc/github-sdk/tree/v1.0.3) for the complete code base.

## Dependency settings for Client application

Now, create your client-side application.

### Setup a static web project

First, execute `npm init .` in a shell, and fill fields it asked you:

    $ npm init .
    This utility will walk you through creating a package.json file.
    It only covers the most common items, and tries to guess sensible defaults.

    See `npm help json` for definitive documentation on these fields
    and exactly what they do.

    Use `npm install <pkg> --save` afterwards to install a package and
    save it as a dependency in the package.json file.

    Press ^C at any time to quit.
    name: (organization-list-js)
    version: (1.0.0)
    description:
    entry point: (index.js)
    test command:
    git repository: (https://github.com/Atry/organization-list-js.git)
    keywords: microbuilder github
    author: Yang Bo
    license: (ISC) MIT
    About to write to /Users/twer/workspace/organization-list-js/package.json:

    {
      "name": "organization-list-js",
      "version": "1.0.0",
      "description": "",
      "main": "index.js",
      "scripts": {
        "test": "echo \"Error: no test specified\" && exit 1"
      },
      "repository": {
        "type": "git",
        "url": "git+https://github.com/Atry/organization-list-js.git"
      },
      "keywords": [
        "microbuilder",
        "github"
      ],
      "author": "Yang Bo",
      "license": "MIT",
      "bugs": {
        "url": "https://github.com/Atry/organization-list-js/issues"
      },
      "homepage": "https://github.com/Atry/organization-list-js#readme"
    }


    Is this ok? (yes)

### Add SDK dependencies

Then, install `microbuilder-js`, `browser-request` `github-sdk` as dependencies:

```
npm install --save http://central.maven.org/maven2/com/thoughtworks/microbuilder/microbuilder-js/1.0.0/microbuilder-js-1.0.0-haxe-js-npm.tgz
```

```
npm install --save http://central.maven.org/maven2/com/thoughtworks/microbuilder/tutorial/github-sdk_2.11/1.0.3/github-sdk_2.11-1.0.3-haxe-js-npm.tgz
```

```
npm install --save browser-request
```

You will use the NPM package [browser-request](https://www.npmjs.com/package/browser-request) with Github SDK for this static web project.
If you are building an Node.js project, use [request](https://www.npmjs.com/package/request) instead.

### The web page

Now we create a web page that queries the organizations of a Github user.

    <!DOCTYPE html>
    <html>
      <head>
        <script type="text/javascript">
          function updateOrganizationList(username) {
            // TODO: Not implemented
          }
        </script>
      </head>
      <body>
        Github user name: <input type="text" onchange="updateOrganizationList(this.value)"/>
        <hr/>
        <ul id="organization-list">
        </ul>
      </body>
    </html>

When the user input a Github user name in the input box,
the `onchange` event will be triggered and the browser will send a request to Github API,
and fill the response into `organization-list`.

### Initialize the SDK

In order to send requests, we will initialize a instance of Github's organization service,
which was defined in Github SDK that you created before.

    <script type="text/javascript" src="node_modules/browser-request/index.js"></script>
    <script type="text/javascript">request = window.returnExports;</script>
    <script type="text/javascript" src="node_modules/microbuilder-js/index.js"></script>
    <script type="text/javascript" src="node_modules/github-sdk/index.js"></script>
    <script type="text/javascript">
      var urlPrefix = "https://api.github.com/";

      var routeConfiguration = com.thoughtworks.microbuilder.tutorial.githubSdk.proxy.MicrobuilderRouteConfigurationFactory.routeConfiguration_com_thoughtworks_microbuilder_tutorial_githubSdk_rpc_IOrganizationService();
      var outgoingJsonService = new com.thoughtworks.microbuilder.js.JsOutgoingJsonService(urlPrefix, routeConfiguration, request);
      var organizationService = com.thoughtworks.microbuilder.tutorial.githubSdk.proxy.MicrobuilderOutgoingProxyFactory.outgoingProxy_com_thoughtworks_microbuilder_tutorial_githubSdk_rpc_IOrganizationService(outgoingJsonService);
    </script>

In this example, the dependency libraries are included by `<script>` tags.
Alternatively, you can use [webpack](https://webpack.github.io/) or [browserify](http://browserify.org/) instead.

### Use the SDK

As the `organizationService` has been created,
you can implement `updateOrganizationList` now.

    <script type="text/javascript">
      function updateOrganizationList(username) {
        organizationService.listUserOrganizations(username).then(function(organizations) {
          var organizationList = document.getElementById("organization-list");

          // Clear all child nodes in organization-list
          while (organizationList.childNodes.length > 0) {
            organizationList.removeChild(organizationList.firstChild);
          }

          for (var i = 0; i < organizations.length; i++) {
            var organization = organizations[i];

            var img = document.createElement("img");
            img.src = organization.avatar_url;
            img.width = 20;
            img.height = 20;
            img.title = organization.description;

            var text = document.createTextNode(organization.login);

            var li = document.createElement("li");
            li.appendChild(img);
            li.appendChild(text);

            organizationList.appendChild(li);
          }
        });
      }
    </script>

`organizationService.listUserOrganizations` returns a [Promise](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Promise),
which pass the response data to the function you passed to `then` method.

Then, you use update the page according to the response data.

The example is complete now.
Open `index.html` in a browser, and input you Github username in the text box,
then you will see your Github organization list.

You can find the entire example of client-side application at [organization-list-js](https://github.com/ThoughtWorksInc/organization-list-js).
