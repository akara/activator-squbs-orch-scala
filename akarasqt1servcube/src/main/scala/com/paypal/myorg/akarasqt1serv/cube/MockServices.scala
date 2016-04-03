package com.paypal.myorg.akarasqt1serv.cube


import akka.actor.{Actor, ActorLogging}

import scala.util.{Success, Try}

case class AuthRequest(user: String, password: String)
case class AuthResponse(token: Try[String])
case class RoleRequest(user: String)
case class RoleResponse(role: Try[String])
case class ContentRequest(token: String, role: String, resource: String)
case class ContentResponse(content: Try[String])


class AuthNActor extends Actor with ActorLogging {

  def receive = {
    case AuthRequest(user, password) =>
      log.info("Got authn request for user {}, letting it pass", user)
      sender() ! AuthResponse(Success("justarandomtoken"))
  }
}

class AuthZActor extends Actor with ActorLogging {

  def receive = {
    case RoleRequest(user) =>
      log.info("Got role request from user {}, answering with 'admin'", user)
      sender() ! RoleResponse(Success("admin"))
  }
}

class ContentActor extends Actor with ActorLogging {

  def receive = {
    case ContentRequest(token, role, resource) =>
      log.info("Got content request token: {}, role: {}, resource: {}, sending some mock content")
      sender() ! ContentResponse(Success("Hello, this is some mock content"))
  }
}
