/*
 * Copyright (c) 2013 eBay, Inc.
 * All rights reserved.
 *
 * Contributors:
 */
package com.paypal.myorg.akarasqt1serv.svc

import akka.pattern.AskTimeoutException
import akka.util.Timeout
import com.paypal.myorg.akarasqt1serv.msgs._
import org.squbs.actorregistry.ActorLookup
import org.squbs.unicomplex.RouteDefinition
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.routing.Directives._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

// this class defines our service behavior independently from the service actor
class Akarasqt1servSvc extends RouteDefinition {

  def route =
    path("hello") {
      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to
                  <i>akarasqt1serv</i>
                  on
                  <i>squbs</i>
                  ,
                  <i>spray-routing</i>
                  and
                  <i>spray-can</i>
                  !</h1>
              </body>
            </html>
          }
        }
      }
    } ~
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
}
