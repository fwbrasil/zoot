# Zoot

### Thin reactive framework to provide and consume REST services

# Using zoot

## Contract

Zoot uses Api traits to define the services 'contract':

``` scala
trait SomeApi extends Api {
	
	@endpoint(
        method = RequestMethod.PUT,
        path = "/simple")
    def simpleMethod(someInt: Int): Future[Int]
}
```

Notes:

1. Api methods must return Future, otherwise an exception will be thrown.
2. Apis should always be traits, not classes or abstract classes.
3. To agregate Apis, just use commom trait inheritance.
4. It is possible to use any value or class type as input or output.


### Optional parameters

By default all parameters are required and 'BadRequest' is returned if there is a missing parameter.

To define parameters as optional, just use the Option type:

``` scala
trait SomeApi extends Api {
	
	@endpoint(
        method = RequestMethod.PUT,
        path = "/simple")
    def simpleMethod(someInt: Int, optionalString: Option[String]): Future[Int]
}
```

In this example, the method will be invoked using 'None' for 'optionalString' if the parameter is missing.


### Default parameters

It is possible to define default values for parameters:

``` scala
trait SomeApi extends Api {
	
	@endpoint(
        method = RequestMethod.PUT,
        path = "/simple")
    def simpleMethod(someInt: Int = 11): Future[Int]
}
```

If the 'someInt' is not specified in the request parameters, the default value '11' is used.


### Parametrized paths

Use parametrized paths to extract parameters from the path:

``` scala
trait SomeApi extends Api {
	
	@endpoint(
        method = RequestMethod.GET,
        path = "/users/:user/name")
    def userName(user: Int): Future[String]
}
```

The request for '/users/111/name' will invoke 'userName' using '111' for the 'user' parameter

## Client

The Client object provides Api instances for remote services:

``` scala
val dispatcher: Request => Future[Response[String]] = ???
val client: SomeApi = Client[SomeApi](dispatcher)
```

You need to provide a 'real' dispatcher instance. Zoot provides some default bindings as defined latter.

Once you have the client instance, it is possible to use Api methods as commom method invocations:

``` scala
val future: Future[Int] = client.simpleMethod(11)
val otherFuture: Future[String] = client.userName(22)
```

Please refer to the [Scala Documentation](http://docs.scala-lang.org/overviews/core/futures.html) for more details about Futures.


## Server

The Server object allows to create a server using an Api instance.

``` scala
class SomeService extends SomeApi {
	def simpleMethod(someInt: Int) = Future.success(someInt + 1)
}

val server: Request => Future[Response[String]] = Server[SomeApi](new SomeService)
```

The server is a function that can be used with the different binds as defined latter.

## Bindings

Please refer to the Spray or Finagle documentation for more details on how to create servers and clients.

### [Spray](http://github.com/spray/spray)

#### Client

``` scala
val dispatcher = SprayClient(host = "localhost", port = 8080)
val client: SomeApi = Client[SomeApi](dispatcher)
```

#### Server

``` scala
implicit val system = ActorSystem("SomeSystem")
implicit val timeout = Timeout(1000 millis)

val server = Server[SomeApi](new SomeService)
val sprayServer = new SprayServer(server))
val sprayActor = system.actorOf(Props(sprayServer))

IO(Http) ! Http.Bind(sprayActor, interface = "localhost", port = 8080)
```

### [Finagle](http://github.com/twitter/finagle)

#### Client

``` scala
val builder = ClientBuilder()
    .codec(Http())
    .hosts(s"$host:$port")
    .hostConnectionLimit(10)
    .requestTimeout(1000 millis)

val dispatcher = FinagleClient(builder.build())
val client: SomeApi = Client[SomeApi](dispatcher)
```

#### Server

``` scala
val address = new InetSocketAddress(nodePort)
val builder =
    ServerBuilder()
        .codec(Http())
        .bindTo(address)
        .keepAlive(true)
        .name("SomeServer")

val server = Server[SomeApi](new SomeService)
val finagleServer = FinagleServer(server, builder.build)
```

## Filters

# FAQ

## Why 'zoot'?

The name is a reference to the [Zoot](http://muppet.wikia.com/wiki/Zoot) character from The Muppets Show, inspired by the jazz saxophonist [Zoot Sims](http://de.wikipedia.org/wiki/Zoot_Sims).

https://www.youtube.com/watch?v=CgfZVNv6w2E

"Forgive me [Roy Fielding](http://en.wikipedia.org/wiki/Roy_Fielding) wherever you are!"

## Why 'reactive'?

This is the buzzword of the moment and zoot uses non-blocking asynchronous IO.

## Ok, now all services in the world must provide Api traits?

If the client and server are using scala and zoot, it is a big win to reuse the Api traits to invoke the services. If not, just write by your own. Anyway you need to specify how to invoke services.