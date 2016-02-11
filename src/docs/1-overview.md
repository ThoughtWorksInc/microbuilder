# Overview

**Microbuilder** is a toolkit that helps you build system across micro-services
implemented in various languages communicating via RESTful JSON API.

## Motive

We, some guys in [ThoughtWorks](http://thoughtworks.com/), are working for a client
helping them maintain a large legacy system.

The legacy system were one single J2EE application that contains hundreds of thousands of lines of source code.
The application was tightly coupled and very hard to add new feature.

In the past couple of years,
we turned the system into [Microservice Architecture](http://martinfowler.com/articles/microservices.html).
We experienced a huge improvement in productivity and maintainability during the process.
Nowaday, there are hundreds of microservices in the system,
and we are continuously creating new microservices for new business domains.

These microservices are written in different programming languages
like Scala, Ruby, Java, or JavaScript,
and usually are maintained by different teams spread on different countries.

As a result, the communication between people became one of the most serious topics in our system,
and Microbuilder is designed for communication.

## How Microbuilder works

We designed a language to define RESTful JSON API,
named MIDL (Microbuilder Interface Definition Language).

You can define data structures and endpoints in object-oriented syntax,
with a little annotations like `@:route`.

    @:final class UserProfile {
      public function new() {}
      public var name:String;
      public var email:String;
      public var address:String;
    }

    interface IUserService {
      @:route("GET", "users/{id}")
      function getUser(id:String):UserProfile;
    }


Developers could perform following tasks around MIDL when building microservices:

1.  service developers use MIDL to define their specific domain API.
1.  service developers generate API Documentation from MIDL.
1. service developers generate server-side stubs from MIDL for target languages (including Scala, JavaScript, Java, C#, PHP, C++, ActionScript 3, NekoVM, and Python), then implement the RESTful service with the help of the stubs.
1. service developers generate client-side SDK from MIDL for target languages, and publish the SDK.
1. service users use the client-side SDK in an application, in order to call the service in a RPC flavor.

Note that every task listed above is optional.
For example, you may want to create MIDL to fit the current API of a legacy service,
and generate client-side SDK and documentation for it,
so that other applications will be easy to communicate with the legacy service.
However, you will not generate server-side stubs
because you already have a service implementation before.

## Documentation and other links

Microbuilder is separated into several libraries,
each library has its own documentation and distribution.

### json-stream-core

Universal Serialization Framework for JSON

* [Github](https://github.com/ThoughtWorksInc/json-stream-core)
* [API reference](https://oss.sonatype.org/service/local/repositories/snapshots/archive/com/thoughtworks/microbuilder/json-stream-core/3.0.3-SNAPSHOT/json-stream-core-3.0.3-SNAPSHOT-javadoc.jar/!/jsonStream/index.html)
* [Haxelib](http://lib.haxe.org/p/json-stream-core)
* [Maven](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.thoughtworks.microbuilder%22%20AND%20a%3A%22json-stream-core%22)

### microbuilder-core

Microbuilder runtime and tools for all languages

* [Github](https://github.com/ThoughtWorksInc/microbuilder-core)
* [API reference](https://oss.sonatype.org/service/local/repositories/snapshots/archive/com/thoughtworks/microbuilder/microbuilder-core/3.0.3-SNAPSHOT/microbuilder-core-3.0.3-SNAPSHOT-javadoc.jar/!/com/thoughtworks/microbuilder/core/index.html)
* [Haxelib](http://lib.haxe.org/p/microbuilder-core)
* [Maven](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.thoughtworks.microbuilder%22%20AND%20a%3A%22microbuilder-core%22)

### microbuilder-js

Microbuilder runtime for JavaScript

* [Github](https://github.com/ThoughtWorksInc/microbuilder-js)
* [API reference](https://oss.sonatype.org/service/local/repositories/snapshots/archive/com/thoughtworks/microbuilder/microbuilder-js/2.0.2-SNAPSHOT/microbuilder-js-2.0.2-SNAPSHOT-javadoc.jar/!/com/thoughtworks/microbuilder/js/index.html)
* [Haxelib](http://lib.haxe.org/p/microbuilder-js)
* [NPM package](http://central.maven.org/maven2/com/thoughtworks/microbuilder/microbuilder-js/2.0.1/microbuilder-js-2.0.1-haxe-js-npm.tgz)

### microbuilder-play

Microbuilder runtime for Scala and Play framework

* [Github](https://github.com/ThoughtWorksInc/microbuilder-play)
* [API reference](https://oss.sonatype.org/service/local/repositories/releases/archive/com/thoughtworks/microbuilder/microbuilder-play_2.10/4.0.0/microbuilder-play_2.10-4.0.0-javadoc.jar/!/com/thoughtworks/microbuilder/play/package.html)
* [Maven (For Scala 2.10)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.thoughtworks.microbuilder%22%20AND%20a%3A%22microbuilder-play_2.10%22)
* [Maven (For Scala 2.11)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.thoughtworks.microbuilder%22%20AND%20a%3A%22microbuilder-play_2.11%22)
