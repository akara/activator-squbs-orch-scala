package com.paypal.myorg.akarasqt1serv.msgs

case class OrchestrationRequest(user: String, password: String, resource: String)
case class OrchestrationResponse(role: String, content: String)

case class AuthenticationFailed(msg: String) extends Exception(msg)
case class AuthorizationFailed(msg: String) extends Exception(msg)
case class InvalidResource(msg: String) extends Exception(msg)
case class OrchestrationTimeout(msg: String) extends Exception(msg)

