package com.kodekutters.ripple.core

import akka.actor._
import akka.actor.SupervisorStrategy.Restart
import messages.ConnectionFailed
import scala.concurrent.duration._
import akka.actor.OneForOneStrategy

/**
 * the link to the ripple server,
 * create and supervise the client that connects to the server (via websocket) and
 * forward all messages to this client
 *
 * @param uris the uri address of the ripple server, e.g. "wss://s1.ripple.com:443/"
 */
class RippleLinker(uris: String) extends Actor with ActorLogging with HandlersManager {

  // create the web socket client that connects to the server
  val wsClient = context.actorOf(JWebSocketClient.props(uris, handlerList))

  // supervise the clients ... TODO
  //  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
  //    case _ =>
  //      log.info("\nin RippleLinker supervisorStrategy Restart")
  //      Restart
  //  }

  // handlers registration then the linker receive
  def receive = manageHandlers orElse linkerReceive

  def linkerReceive: Receive = {
    // typically from the client, when no connection could be established
    case ConnectionFailed =>
      log.info("\n......connection failed ")
      // report to the parent that the connection failed
      context.parent ! ConnectionFailed
      context stop self

    // forward all other messages to the client
    case msg => wsClient forward msg
  }

}

object RippleLinker {
  def props(uris: String): Props = Props(new RippleLinker(uris))
}