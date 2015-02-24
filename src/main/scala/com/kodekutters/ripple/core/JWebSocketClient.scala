package com.kodekutters.ripple.core

import akka.actor._
import messages.{JsonMessage, ConnectionFailed, Send}
import play.api.libs.json.Json
import scala.collection.mutable
import java.net.URI
import org.java_websocket.drafts.{Draft_10, Draft}


/**
 * the web socket client that connects to the ripple server
 *
 * @param uris the server uri, e.g. "wss://s1.ripple.com:443/"
 * @param handlerList the list of responses handlers
 */

class JWebSocketClient(uris: String, handlerList: mutable.HashSet[ActorRef]) extends Actor with ActorLogging {

  val headers: Map[String, String] = Map()
  // Map(("Authorization", "Basic " + new sun.misc.BASE64Encoder().encode((rpcUser + ":" + rpcPass).getBytes)) :: Nil: _*)

  // the client receives all server responses and pass them onto the handlers
  val client = new WSClient(URI.create(uris), new Draft_10(), headers, 0) {
    override def onMessage(msg: String) = {
      try {
        // convert the response to json
        val js = Json.parse(msg)
        // forward all responses from the server to the handlers
        handlerList.foreach(handler => handler forward JsonMessage(js))
      } catch {
        case e: Exception => println("\n.....in JWebSocketClient error " + e)
      }
    }
  }

  try {
    client.connect()
    println("connected to: " + client.getURI)
  } catch {
    case e: Exception =>
      println("\n.....error could not connect to: " + client.getURI)
      context.parent ! ConnectionFailed
      context stop self
  }

  // messages received by this actor
  def receive = {
    // send the msg to the server
    case Send(msg) => client.send(msg)

    case x => log.info("RECV: x " + x.toString)
  }

}

object JWebSocketClient {
  def props(uris: String, handlerList: mutable.HashSet[ActorRef]): Props = Props(new JWebSocketClient(uris, handlerList))
}