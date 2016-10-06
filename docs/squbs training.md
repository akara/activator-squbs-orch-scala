##squbs Training - Your first Async Orchestrator

###Prerequisistes for this exercise

- JDK 8. This code has been tested with 1.8.0_60 and should work with later versions of Java 8.
- IntelliJ is preferred as the IDE as the instructions are based on IntelliJ. We need these plugins for IntelliJ:
   - The Scala plugin
   - The sbt plugin - this plugin is not on the primary list of plugins. You need to click `Browse repositories...` and search for **sbt** from this screen.


###Get the squbs template application up and running:
1. Clone this repo into a local project.
2. Use IntelliJ to open this project by opening the `build.sbt` file from the root project (not the subprojects). Opening the root project directory directly works most of the time but is not as reliable as selecting the `build.sbt` file.
3. Configure `run` in IntelliJ
   * Click down button and choose “Edit Configurations"
   * Click the `+` sign
   * Choose `application`
   * Name: `RunApp`
   * Main class: `org.squbs.unicomplex.Bootstrap`
   * Working directory: `orchsamplesvc`
   * Use classpath of module: `orchsamplesvc`
   * Before launch: … box click `-` to remove `make` and click `+` to choose `SBT`. Select action `test:products`, the default.
   * Click `OK`
4. Run the app by pressing the start button with the right arrow
5. Check the app and registered context
   * Point your browser to `http://localhost:8080/adm` for the admin console. Note: The result is JSON and it is useful to have a JSON plugin in your browser allowing URL navigations.
   * To ensure the application listens to the root context, find the `Listeners` link or go directly to `http://localhost:8080/adm/bean/org.squbs.unicomplex:type~Listeners`. You'll see the application registered to the root context `""` and the admin console registered to the `"adm"` context on the `default-listener`.
   * To see all the registered "cubes" or modules, find the `Cubes` link or go directly to `http://localhost:8080/adm/bean/org.squbs.unicomplex:type~Cubes`. You'll see `OrchSampleCube` and `OrchSampleSvc` registered. The `orchsamplemsgs` project only defines the message types for communication between `cubes`. It does not define a `cube` by itself.
   * 8. Point your browser to `http://localhost:8080/hello`

    **Note**: The web context is registered in `squbs-meta.conf` of the service project. It can be any value. The project template is setup to use the root context `""` by default.
6. See your application in action.10. Stop the app.
7. Run the app from sbt
   * Go to sbt window
   * `project orchsamplesvc`
   * `~re-start` (The `~` is telling sbt to re-run that command every time a change is detected, like `~compile` would recompile every time a file changes
12. Make a modification to the app.
   * Edit the `SampleOrchestrationService.scala` file under the `orchsamplesvc` project
   * Change "SampleOrchestrationService" name to "My Service" in the response xml
   * Save
13. Watch sbt re-starting your app
14. Stop the app in sbt
   * `re-stop`

###Architecture of Simple Orchestration App:
![Orchestrator Architecture](orchestrator.png)

###Disclaimer:
The following steps are already done for this sample project you can remove the files and start over to learn getting it together from scratch, or just review the steps to get a clear understanding what the project does.

###Create messages for the mock services:

1. In the cube project, create `src/main/scala` if not exists
2. Create a new Scala class: MockServices.scala
   * Right click on the `src/main/scala` directory in the project pane
   * Select `new` -> `Scala Class`
   * Enter class name with package: `org.squbs.orchsample.cube.MockServices`
3. Implement all the request/response types to the mock service before and outside any class:

   ```scala
   case class AuthRequest(user: String, password: String)
   case class AuthResponse(token: Try[String])
   case class RoleRequest(user: String)
   case class RoleResponse(role: Try[String])
   case class ContentRequest(token: String, role: String, resource: String)
   case class ContentResponse(content: Try[String])
   ```
   Note IntelliJ may suggest certain imports. Accept the suggestions. Sometimes there are multiple choices. For `Try` import `scala.util.Try`.

###Create and register actors mocking the services to call
1. In the same file, remove the `MockService` class and enter the mock actors:
   * AuthNActor
   
   ```scala
   class AuthNActor extends Actor with ActorLogging {

     def receive = {
       case AuthRequest(user, password) =>
         log.warning("Got authn request for user {}, letting it pass", user)
         sender() ! AuthResponse(Success("justarandomtoken"))
     }
   }
   ```
     
   * AuthZActor
   
   ```scala
   class AuthZActor extends Actor with ActorLogging {

     def receive = {
       case RoleRequest(user) =>
         log.warning("Got role request from user {}, answering with 'admin'", user)
         sender() ! RoleResponse(Success("admin"))
     }
   }
   ```

   * ContentActor
   
   ```scala
   class ContentActor extends Actor with ActorLogging {

     def receive = {
       case ContentRequest(token, role, resource) =>
         log.warning("Got content request token: {}, role: {}, resource: {}, sending some mock content",
           token, role, resource)
         sender() ! ContentResponse(Success("Hello, this is some mock content"))
     }
   }
   ```
   
2. Register the actors. This registration tells squbs to start your actors for you as long-running actors. Registering the types allows squbs ActorRegistry to find actors by input and output type. Note: Registering the input/output types is optional and only needed for type-based lookup in the ActorRegistry.

   Open file `squbs-meta.conf` in the cube project and replace it to be the followings:

  ```
  cube-name = org.squbs.OrchSampleCube
  cube-version = "0.0.1-SNAPSHOT"
  squbs-actors = [
    {
      class-name = org.squbs.orchsample.cube.AuthNActor
      name = authNActor
      message-class = [
        {
          request = org.squbs.orchsample.cube.AuthRequest
          response = org.squbs.orchsample.cube.AuthResponse
        }
      ]
    }
    {
      class-name = org.squbs.orchsample.cube.AuthZActor
      name = authZActor
      message-class = [
        {
          request = org.squbs.orchsample.cube.RoleRequest
          response = org.squbs.orchsample.cube.RoleResponse
        }
      ]
    }
    {
      class-name = org.squbs.orchsample.cube.ContentActor
      name = contentActor
      message-class = [
        {
          request = org.squbs.orchsample.cube.ContentRequest
          response = org.squbs.orchsample.cube.ContentResponse
        }
      ]
    }
  ]
  ```

###Create the orchestration messages
In the orchsamplemsgs project, create new case class OrchestrationRequest/OrchestrationResponse. This is for service to send request/response to the orchestrator logic.

1. If the `src/main/scala` does not exist, create the directory by right-clicking on `src/main` in the project pane, select `New`->`Directory` and name the directory `scala`.
2. Right click on the `scala` directory and create a new Scala class by selecting `New`->`Scala Class`. Then name the class `org.squbs.orchsample.msgs.OrchestrationMessages`.
3. Enter the following content in the class.

   ```scala
   case class OrchestrationRequest(user: String, password: String, resource: String)
   case class OrchestrationResponse(role: String, content: String)
   ```

4. Also create a few known exception cases:

   ```scala
   case class AuthenticationFailed(msg: String) extends Exception(msg)
   case class AuthorizationFailed(msg: String) extends Exception(msg)
   case class InvalidResource(msg: String) extends Exception(msg)
   case class OrchestrationTimeout(msg: String) extends Exception(msg)
   ```



###Create the orchestrator actor and orchestration dispatcher
The orchestration actor is a short-lived actor that only lives one request as it contains the request's intermediate state. Services can create the orchestration actor at will. But to provide through loose coupling from http service and be able to call it from any kind of client, be it messages or otherwise, we front the orchestrator with a long-living orchestration dispatcher.

**Note**: The dispatcher has nothing to do with the Akka dispatcher. The name duplication is just coincidence)

1. Edit the `build.sbt` file in `orchsamplecube` project and add dependencies to `squbs-pattern` and `squbs-actorregistry` The dependencies will look like below. When saving, IntelliJ will prompt for refreshing the project. Click `Refresh project` on the top of the editor. This may take some time depending on bandwidth.

   ```scala
   libraryDependencies ++= Seq(
     "com.typesafe.akka" %% "akka-actor" % akkaV,
     "com.typesafe.akka" %% "akka-slf4j" % akkaV,
     "org.squbs" %% "squbs-unicomplex" % squbsV,
     "org.squbs" %% "squbs-pattern" % squbsV,
     "org.squbs" %% "squbs-actorregistry" % squbsV,
     "org.scalatest" %% "scalatest" % "2.2.1" % "test",
     "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
     "org.squbs" %% "squbs-testkit" % squbsV % "test"
   )
   ```
   
2. Create a new Scala class `ContentOrchestrator.scala` in the `orchsamplecube` project.
   * Right click on the `src/main/scala` directory in the project pane
   * Select `new` -> `Scala Class`
   * Enter class name with package: `org.squbs.orchsample.cube.ContentOrchestrator`

3. Create the ContentOrchestrator actor.

   ```scala
   class ContentOrchestrator extends Actor with Orchestrator {
   
   }
   ```
   **Note**: We'll implement the ContentOrchestrator from bottom to top, a bit backwards as to not have any dependency issues.
4. Create the request/response functions to call the mock services. Add these functions to the `ContentOrchestrator` class.
 
   ```scala
     def authenticate(user: String, password: String): OFuture[String] = {
       val p = OPromise[String]
       ActorLookup ! AuthRequest(user, password)
       expectOnce {
         case AuthResponse(token) => p.complete(token)
       }
       p.future
     }

     def authorize(user: String): OFuture[String] = {
       val p = OPromise[String]
       ActorLookup ! RoleRequest(user)
       expectOnce {
         case RoleResponse(role) => p.complete(role)
       }
       p.future
     }

     def fetchContent(resource: String)(token: String, role: String): OFuture[String] = {
       val p = OPromise[String]
       ActorLookup ! ContentRequest(token, role, resource)
       expectOnce {
         case ContentResponse(content) => p.complete(content)
       }
       p.future
     }
   ```

   **Note**: You may notice that we just use `ActorLookup` as is, without telling what to lookup for. `ActorLookup` tries to figure out by itself what you want, based on the request type provided in the `!` and the ones registered in `squbs-meta.conf`. There are other options of using ActorLookup, by request type, response type, by name, and combination of those. We just show the simple use cases
   
5. Implement the orchestrate function and orchestration logic. The orchestrate function is the key orchestration logic and is usually placed above the request/response functions.

   ```scala
     def orchestrate(request: OrchestrationRequest, requester: ActorRef): Unit = {
       import request._
       val tokenF = authenticate(user, password)
       val roleF = authorize(user)
       val contentF = (tokenF, roleF) >> fetchContent(resource)

       // Happy path
       for {
         role <- roleF
         content <- contentF
       } {
         requester ! OrchestrationResponse(role, content)

         context.stop(self)
       }
       ...
   ```
   
6. Also take care of the not so happy paths:

   ```scala
       ...
       // Not so happy cases
       tokenF onFailure {
         case e: Throwable =>
           requester ! Status.Failure(e)
           context.stop(self)
       }

       roleF onFailure {
         case e: Throwable =>
           requester ! Status.Failure(e)
           context.stop(self)
       }

       contentF onFailure {
         case e: Throwable =>
           requester ! Status.Failure(e)
           context.stop(self)
       }
       ...
   ```

7. Don't forget to handle timeouts properly, too. But before we do that, we need to import a couple of utilities at the top of the file:

   ```scala
   import scala.concurrent.duration._
   import scala.language.postfixOps
   ```
   
   Now we continue with the rest of the orchestrate function:
   
   ```scala
       import context.dispatcher
       val timeout = Timeout(50 milliseconds)
       context.system.scheduler.scheduleOnce(timeout.duration, self, timeout)
       expectOnce {
         case Timeout(duration) =>
           val checks = Seq(tokenF -> "token", roleF -> "role", contentF -> "content")
           val message = checks.collect {
             case (future, name) if !future.isCompleted => name
           } .mkString("Timed out waiting for: [", ",", s"] after $duration")
           requester ! Status.Failure(OrchestrationTimeout(message))
           context.stop(self)
       }
     }   
   ```
   
   **Note**: If we are called by an `ask` or `?`, then we can send a `akka.actor.Status.Failure` as the response to indicate the failure. This will cause the Future held as part of the ask to fail. Since the Orchestrator is usually called with `?`, the best path of error handling is actually using this `akka.actor.Status.Failure` message type. 
   
8. Expect initial orchestration request. This code is to be put ahead of the orchestration function in the ContentOrchestrator class.

   ```scala
     expectOnce {
       case request: OrchestrationRequest => orchestrate(request, sender())
     }
   ```
   
   
9. Create the ContentOrchestrator dispatcher. Remember, the ContentOrchestrator is a single use actor. It gets created per request. So we need to implement the orchestration dispatcher as a long-running, registered actor:

   ```scala
   class OrchDispatcher extends Actor {
     def receive = {
       case request: OrchestrationRequest => context.actorOf(Props[ContentOrchestrator]) forward request
     }
   }
   ```
   
10. Last, we need to register the orchestration dispatcher. Edit `squbs-meta.conf` of the cube project and add the following actor registration:

   ```
     {
       class-name = org.squbs.orchsample.cube.OrchDispatcher
       name = contentOrchestrator
       message-class = [
         {
           request = org.squbs.orchsample.msgs.OrchestrationRequest
           response = org.squbs.orchsample.msgs.OrchestrationResponse
         }
       ]
     }
   ```

###Modify the Http service to call the orchestrator
1. Open the route class `SampleOrchestrationService` in the `orchsamplesvc` project.
2. If there is a `Mediator` actor in the source, remove it.
3. Add the following directives to the route. This will route a request with `http://localhost:8080/content/foo?user=sombody&pass=somepass` to the orchestrator:

   ```scala
   ~
    path("content" / Segment) { resource =>
      get {
        parameters('user, 'pass) { (user, pass) =>
          import context.dispatcher
          implicit val timeout = Timeout(100 milliseconds)
          onComplete(ActorLookup ? OrchestrationRequest(user, pass, resource)) {
            case Success(OrchestrationResponse(role, content)) => complete(content)
            case Failure(AuthenticationFailed(msg)) => complete(StatusCodes.Unauthorized, msg)
            case Failure(AuthorizationFailed(msg)) => complete(StatusCodes.Unauthorized, msg)
            case Failure(InvalidResource(msg)) => complete(StatusCodes.NotFound, msg)
            case Failure(OrchestrationTimeout(msg)) => complete(StatusCodes.RequestTimeout, msg)
            case Failure(e: AskTimeoutException) => complete(StatusCodes.RequestTimeout, e.getMessage)
            case Failure(e) => complete(StatusCodes.InternalServerError, e.getMessage)
            case e => complete(StatusCodes.InternalServerError, s"Unknown error: $e")
          }
        }
      }
    }
   ```
   
   These directives will match the path and parameters, call the orchestrator asynchronously, and interpret the response from the orchestrator into HTTP status codes.

###Run the project
1. Re-run your project and test with the following URL from your browser: `http://localhost:8080/content/foo?user=sombody&pass=somepass`
2. Optional, add time delays to authentication or authorization actors and see the timeout behavior as well as the asynchronous behavior through logging, as in the example below:

   ```scala
   class AuthZActor extends Actor with ActorLogging {

     def receive = {
       case RoleRequest(user) =>
         log.warning("Got role request from user {}, answering with 'admin'", user)
         import context.dispatcher
         context.system.scheduler.scheduleOnce(300 milliseconds, sender(), RoleResponse(Success("admin")))
     }
   }

   ```
   
   **NOTE**: It is a **crime** to call Thread.sleep in this architecture. It blocks the thread and brings your application to a crawl instantaneously. If you need to make sure something happens in the future, use the system scheduler. This can be accessed from an actor through `context.system.scheduler`.
   
###Finally, go hack and have fun!