/*
 * Copyright (c) 2013 eBay, Inc.
 * All rights reserved.
 *
 * Contributors:
 */
package com.paypal.myorg.akarasqt1serv.svc

import akka.actor.Props
import com.paypal.myorg.akarasqt1serv.msgs._
import org.json4s.{DefaultFormats, Formats, NoTypeHints}
import org.squbs.unicomplex.RouteDefinition
import spray.http.MediaTypes._
import spray.httpx.Json4sJacksonSupport
import spray.httpx.encoding.Gzip
import spray.routing.Directives._

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
      path("events") {
        get { ctx =>
          context.actorOf(Props(classOf[Mediator], ctx))
        }
      }
}
