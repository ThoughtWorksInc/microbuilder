# Interface definition

An `interace` in MIDL describes a set of endpoints.

For example, this is an MIDL file that defines two endpoints:

    // This file is at src/haxe/userSdk/rpc/Rpc.hx
    package userSdk.rpc;

    // Import Future, which holds asynchronous operations.
    import jsonStream.rpc.Future;

    // Import Profile, which should be defined in src/haxe/userSdk/model/Models.hx
    import userSdk.model.Models.Profile;

    @:structuralFailure(userSdk.model.Models.Failure)
    @:nativeGen
    interface IUserProfileService {

      @:route("GET", "users/{userId}")
      @:responseContentType("text/json")
      function getProfile(userId:String):Future<Profile>;

      @:requestHeader("Your-Custom-Header", "custom-header-value")
      @:route("PUT", "users/{userId}")
      @:requestContentType("text/json")
      function setProfile(userId:String, profile:Profile):Future<Void>;

    }

    @:nativeGen
    interface IMyOtherService {
      // Put other endpoint definitions here...
    }

## Package and directory layout

MIDL uses Haxe's [`package` and `import` syntax](http://haxe.org/manual/type-system-import.html) to locate MIDL files.
The file must be at the path corresponds to current package.
For example,
if you configured source path at `src/haxe` and set `package` is `userSdk.rpc`,
then the file must be under `src/haxe/userSdk/rpc/` directory.

Unlike Java, Haxe's source file may contains multiple root types,
and the file name does not have to be the same as the type name.
So you could name the MIDL file as `Rpc.hx` and put `IUserProfileService` and `IMyOtherService` interface in it.

## Interfaces and methods

MIDL uses Haxe's interface and method syntax to define endpoints.
Then, Microbuilder compiler can generate the implementation of these methods into client-side SDKs.
Applications could call these methods in SDK to send HTTP requests.

See [Annotations](4-annotations.html) for more information about mapping between RESTful JSON API and methods.
