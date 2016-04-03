package com.paypal.myorg.akarasqt1serv.msgs

import scala.util.Try

case class OrchestrationRequest(user: String, password: String, resource: String)
case class OrchestrationResponse(result: Try[(String, String)])

case class AuthenticationFailed(msg: String) extends Exception(msg)
case class AuthorizationFailed(msg: String) extends Exception(msg)
case class InvalidResource(msg: String) extends Exception(msg)
case class OrchestrationTimeout(msg: String) extends Exception(msg)

