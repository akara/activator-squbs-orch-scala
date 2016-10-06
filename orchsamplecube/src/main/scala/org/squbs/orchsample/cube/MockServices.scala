package org.squbs.orchsample.cube

import akka.actor.{Actor, ActorLogging}
import org.squbs.orchsample.msgs.AuthenticationFailed

import scala.util.{Failure, Success, Try}

case class AuthRequest(user: String, password: String)
case class AuthResponse(token: Try[String])
case class RoleRequest(user: String)
case class RoleResponse(role: Try[String])
case class ContentRequest(token: String, role: String, resource: String)
case class ContentResponse(content: Try[String])


class AuthNActor extends Actor with ActorLogging {

  def receive = {
    case AuthRequest("foo", "bar") =>
      log.warning("Got authn request for user foo, letting it pass")
      sender() ! AuthResponse(Success("justarandomtoken"))
    case AuthRequest(user, password) =>
      log.warning("Got authn request for user {}, denying", user)
      sender() ! AuthResponse(Failure(AuthenticationFailed("Only accepts user foo pass bar for now")))
  }
}

class AuthZActor extends Actor with ActorLogging {

  def receive = {
    case RoleRequest(user) =>
      log.warning("Got role request from user {}, answering with 'admin'", user)
      sender() ! RoleResponse(Success("admin"))
  }
}

class ContentActor extends Actor with ActorLogging {

  def receive = {
    case ContentRequest(token, role, resource) =>
      log.warning("Got content request token: {}, role: {}, resource: {}, sending some mock content",
        token, role, resource)
      sender() ! ContentResponse(Success("Hello, this is some mock content"))
  }
}
