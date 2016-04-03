package com.paypal.myorg.akarasqt1serv.cube

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import com.paypal.myorg.akarasqt1serv.msgs.{OrchestrationRequest, OrchestrationResponse, OrchestrationTimeout}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}


class Dispatcher extends Actor {
  def receive = {
    case request: OrchestrationRequest => context.actorOf(Props[ContentOrchestrator]) forward request
  }
}

class ContentOrchestrator extends Orchestrator {

  expectOnce {
    case request: OrchestrationRequest => orchestrate(request, sender())
  }

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
      requester ! OrchestrationResponse(Success((role, content)))
      context.stop(self)
    }

    // Not so happy cases
    tokenF onFailure {
      case e: Throwable =>
        requester ! OrchestrationResponse(Failure(e))
        context.stop(self)
    }

    roleF onFailure {
      case e: Throwable =>
        requester ! OrchestrationResponse(Failure(e))
        context.stop(self)
    }

    contentF onFailure {
      case e: Throwable =>
        requester ! OrchestrationResponse(Failure(e))
        context.stop(self)
    }

    import context.dispatcher
    val timeout = Timeout(50 milliseconds)
    context.system.scheduler.scheduleOnce(timeout.duration, self, timeout)
    expectOnce {
      case Timeout(duration) =>
        val checks = Seq(tokenF -> "token", roleF -> "role", contentF -> "content")
        val message = checks.collect {
          case (future: OFuture[_], name: String) if !future.isCompleted => name
        } .mkString("Timed out waiting for: [", ",", s"] after ${timeout.duration}")
        requester ! OrchestrationResponse(Failure(OrchestrationTimeout(message)))
        context.stop(self)
    }
  }

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
}
