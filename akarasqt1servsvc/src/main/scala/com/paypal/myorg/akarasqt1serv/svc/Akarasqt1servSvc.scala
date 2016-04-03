/*
 * Copyright (c) 2013 eBay, Inc.
 * All rights reserved.
 *
 * Contributors:
 */
package com.paypal.myorg.akarasqt1serv.svc

import akka.actor.Props
import com.paypal.myorg.akarasqt1serv.msgs._
import org.squbs.unicomplex.RouteDefinition
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.routing.Directives._

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
        parameters('user.as[String], 'pass.as[String]) { (user, pass) =>
          onComplete(ActorLookup ? OrchestrationRequest(user, pass, resource)) {
            case Success(OrchestrationResponse(Success((role, content)))) => complete(content)
            case Success(OrchestrationResponse(Failure(e @ AuthenticationFailed(msg)))) =>
              complete(StatusCodes.Unauthorized, msg)
            case Success(OrchestrationResponse(Failure(e @ AuthorizationFailed(msg)))) =>
              complete(StatusCodes.Unauthorized, msg)
            case Success(OrchestrationResponse(Failure(e @ InvalidResource(msg)))) =>
              complete(StatusCodes.NotFound, msg)
            case Success(OrchestrationResponse(Failure(e @ OrchestrationTimeout(msg)))) =>
              complete(StatusCodes.RequestTimeout, msg)
            case Failure(e) => complete(StatusCodes.InternalServerError, e.getMessage)
            case _ => complete(StatusCodes.InternalServerError, "Unknown error")
          }
        }
      }
    }
}
