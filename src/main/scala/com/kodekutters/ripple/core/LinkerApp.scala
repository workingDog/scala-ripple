package com.kodekutters.ripple.core

import akka.actor.{ActorRef, ActorSystem}
import messages.{Send, Register}

/**
 * convenience base class for a Ripple application
 */
class LinkerApp {

  implicit val system = ActorSystem("RippleSession")

  // create the ripple server linker
  val linker = system.actorOf(RippleLinker.props("wss://s1.ripple.com:443/"))

  // give the linker time to connect
  Thread.sleep(2000)

  // register the handler
  def withHandler(handler: ActorRef) = linker ! Register(handler)

  // send the request
  def sendRequest(request: String) = linker ! Send(request)

}
