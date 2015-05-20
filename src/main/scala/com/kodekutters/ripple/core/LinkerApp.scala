package com.kodekutters.ripple.core

import akka.actor.{ActorRef, ActorSystem}
import messages.{Send, Register}

/**
 * convenience base class for using the Ripple server
 */
class LinkerApp {

  implicit val system = ActorSystem("RippleSession")

  // create the ripple server linker
  implicit val linker = system.actorOf(RippleLinker.props("wss://s1.ripple.com:443/"))

  // give the linker time to connect
  Thread.sleep(2000)

  // register a handler
  def withHandler(handler: ActorRef) = linker ! Register(handler)

  // send the request
  def sendRequest(request: String) = linker ! Send(request)

}
