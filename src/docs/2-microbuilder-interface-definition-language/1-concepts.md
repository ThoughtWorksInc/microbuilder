# Concepts

**MIDL** (**M**icrobuilder **I**nterface **D**efinition **L**anguage)
is the language that describes RESTful JSON API crossing services.

MIDL is based on [Haxe](http://haxe.org/),
with some MIDL-specific additional annotations.
So a MIDL source file is also a Haxe source file,
though we only need a subset of Haxe features to describe RESTful JSON API.

Haxe is an object-oriented language.
MIDL reuses Haxe's interface, method and class definition syntax.

In order to define a RESTful JSON API,
you can create a Haxe interface,
in which there are method declarations.
Every method corresponds to a specific endpoint.

    interface IUserProfileService {

      @:route("GET", "users/{userId}")
      function getProfile(userId:String):Future<Profile>;

      @:route("PUT", "users/{userId}")
      @:requestContentType("text/json")
      function setProfile(userId:String, profile:Profile):Future<Void>;

    }

Then, you need to define `Profile` referenced above,
which corresponds to a JSON data structure.

    @:final class UserProfile {
      public function new() {}

      public var name:String;

      public var email:String;

      public var age:Int;

    }

As a result, the above MIDL describes a RESTful JSON service that has the following behaviors:

 * When a client sends a GET request to the service at `/users/user-id`,
   the server returns a JSON response of the user profile data for `user-id`
   like `{ "name": "User Name", "email": "user@host.com", "age": 18 }`.
 * When a client sends a PUT request to the service at `/users/user-id` with a JSON content
   like `{ "name": "User Name", "email": "user@host.com", "age": 18 }`,
   the server returns no data but HTTP status.
