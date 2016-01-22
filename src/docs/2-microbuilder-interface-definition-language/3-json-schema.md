# JSON schema

In MIDL, we use classes to describe the JSON schema for request content or response content.
Client-side SDKs and server-side stubs will convert class instances from/to JSON.

## Request content JSON schema

A MIDL method that has a [`@:requestContentType`](4-annotations.html#3)
describes an endpoint that requires a request content body.
For these method, the last parameter will be send to server as request content body,
and the type of the last parameter references to a JSON schema for the request content body.

    @:nativeGen
    interface IMyService {
      @:requestContentType("text/json")
      @:route("PUT", "users/{userId}")
      function setProfile(userId:String, contentBody:RequestType):Future<Void>;
    }

In the above code block, `RequestType` must be a type that describes JSON schema for request content body.

## Response content JSON schema

The return type of a MIDL method must be `jsonStream.rpc.Future<ResponseType>`,
where `ResponseType` is the data structure of JSON for HTTP response.

    @:nativeGen
    interface IMyService {
      @:responseContentType("text/json")
      @:route("PUT", "users/{userId}")
      function getProfile(userId:String):Future<ResponseType>;
    }

In the above code block, `RequestType` must be a type that describes JSON schema for response content body.

## Built-in types for JSON schema

### Primary types

MIDL reuses some standard types in Haxe language as primary types of JSON schema.

  * [Int](http://api.haxe.org/Int.html) and [Float](http://api.haxe.org/Int.html) correspond to JavaScript `Number` literal, e.g. `1.2`, `-3.4`, `3.2349E-13`
  * [String](http://api.haxe.org/String.html) corresponds to JavaScript `String` literal, e.g. `"string value"`.
  * [Bool](http://api.haxe.org/String.html) corresponds to JavaScript `Boolean` literal, which could be `false` or `true`.

### Array types

In MIDL, [haxe.ds.Vector](http://api.haxe.org/haxe/ds/Vector.html) corresponds to JavaScript `Array` literal,
and the type parameter of `Vector` is the element type.

For example, `Vector<Int>` accepts JSON like `[2, 4, 5]`, and `Vector<Bool>` accepts JSON like `[false, true, true]`.

## Custom class types

In MIDL, a `class` defines the schema of a JSON object literal.

For example,

    // This file is at src/haxe/userSdk/model/Models.hx
    package userSdk.model;

    @:final class UserProfile {
      public function new() {}
      public var name:String;
      public var email:String;
      public var age:Int;
    }

This class accepts JSON like `{ "name": "Zhang San", "email": "zs@host.com", age: 18 }`.

Note that you must not omit the constructor `public function new() {}`,
otherwise client-side SDKs and server-side stubs will not be able to convert a `UserProfile` from/to JSON.
