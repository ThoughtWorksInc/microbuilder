# Annotations

MIDL extends Haxe language with some annotations.

## `@:structuralFailure`

`@:structuralFailure` can be put in front of a MIDL interface.
It specifies what format of data the service will return
when the server produces 4xx or 5xx HTTP status.

For example:

    @:structuralFailure(package.FileName.TypeName)
    interface IMyService {
      // Put your endpoint definitions here...
    }

This annotation affect [client-side SDKs](../3-client-side-sdks/1-overview.html)'s behavior of parsing response.
Client-side SDKs will parse the response content as a JSON with a schema defined in `package.FileName.TypeName`,
when the server produces 4xx or 5xx HTTP status.

## `@:nativeGen`

`@:nativeGen` can be put in front of a MIDL interface or a MIDL class.
It annotates that the MIDL interface should be compiled to a native interface of target platforms.

    @:nativeGen
    interface IMyService {
      // Put your endpoint definitions here...
    }

`@:nativeGen` prevents Haxe compiler generating helper methods for Haxe refactor API on the service.
You'd better always specify `@:nativeGen` for interfaces unless you want to use Haxe refactor API.

## `@:route`

`@:route` must be put in front of a MIDL method.
It specifies the HTTP method and URI correspond to this method.

    @:nativeGen
    interface IMyService {
      @:route("GET", "users/{userId}")
      function getProfile(userId:String):Future<Profile>;
    }

The first parameter is the HTTP method, e.g. GET, POST, PUT, HEAD...

The second parameter is the URI template.
Variables in `{...}` will be replaced to parameters of the MIDL method.
The specification of URI template can be found at [RFC6570](https://tools.ietf.org/html/rfc6570).
Right now, Microbuilder support [level 1](https://tools.ietf.org/html/rfc6570#section-1.2) features of URI template.

## `@:requestContentType`

`@:requestContentType` can be put in front of a MIDL method.
It specifies what `Content-Type` HTTP header that the client will send to the server.

    @:nativeGen
    interface IMyService {
      @:requestContentType("text/json")
      @:route("PUT", "users/{userId}")
      function setProfile(userId:String, contentBody:Profile):Future<Void>;
    }

You must not set this annotation for a GET or HEAD method,
while you must always set this annotation for a POST or PUT method.

Note that the last parameter will become request content body if you specifies a `@:requestContentType`.

## `@:responseContentType`

`@:responseContentType` can be put in front of a MIDL method.
It specifies what `Content-Type` HTTP header that the server will send to the client.

    @:nativeGen
    interface IMyService {
      @:responseContentType("text/json")
      @:route("PUT", "users/{userId}")
      function getProfile(userId:String):Future<Profile>;
    }

## `@:requestHeader`

`@:requestHeader` can be put in front of a MIDL method.
It adds custom headers into request.

    @:nativeGen
    interface IMyService {
      @:requestHeader("Custom-Request-Header", "custom-header-value")
      @:requestHeader("Session-Id", sessionId)
      @:requestContentType("text/json")
      @:route("PUT", "users/{userId}")
      function setProfile(userId:String, sessionId:String, contentBody:Profile):Future<Void>;
    }

You can specify a string literal for a fixed header value, like `@:requestHeader("Custom-Request-Header", "custom-header-value")`.
However, sometimes you want to map a parameter to a header value, like `@:requestHeader("Session-Id", sessionId)`.

## `@:final`

`@:final` can be put in front of a MIDL class.
It prevent the class being extened and will affect the behavior of mapping between JSON and class instance.
See [Custom class types](3-json-schema.html#custom-class-types) section for more information.

## `@:transient`

`@:transient` can be put in front of a MIDL class.
A `@:transient` field will be ignored when mapping to JSON.

For example, given a definition for `MyData`

    @:final
    class MyData {
      public function new() {}
      public var notTransientField:Int = 1;
      @:transient public var transientField:Int = 2;
    }

Then, create a `MyData` with default intial filed values: `new MyData()`.
It will become `{ "notTransientField": 1 }`.
`transientField` will not be present in the JSON because of the `@:transient`.
