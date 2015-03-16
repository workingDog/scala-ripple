package com.kodekutters.ripple.core

import akka.actor._
import messages.{JsonMessage, ConnectionFailed, Send}
import org.java_websocket.handshake.ServerHandshake
import play.api.libs.json.Json
import scala.collection.mutable
import java.net.URI
import org.java_websocket.drafts.Draft_17
import collection.JavaConversions._
import java.util.Base64


/**
 * the web socket client that connects to the ripple server
 *
 * @param uris the server uri, e.g. "wss://s1.ripple.com:443/"
 * @param handlerList the list of responses handlers
 */

class JWebSocketClient(uris: String, handlerList: mutable.HashSet[ActorRef]) extends Actor with ActorLogging {

  val headers: java.util.Map[String,String] = mutable.Map[String,String]()
  // Map(("Authorization", "Basic " + Base64.getUrlEncoder.encodeToString((user + ":" + pass).getBytes)) :: Nil: _*)

  // the client receives all server responses and pass them onto the handlers
  val client = new WSClient(URI.create(uris), new Draft_17(), headers, 0) {
    // receive the server responses
    override def onMessage(msg: String) = {
      try {
        // convert the response message to json
        val js = Json.parse(msg)
        // forward all responses from the server to the handlers
        handlerList.foreach(handler => handler forward JsonMessage(js))
      } catch {
        case e: Exception => println("\n.....in JWebSocketClient error " + e)
      }
    }

    override def onOpen(handshakeData: ServerHandshake) = {
      println("websockets connecting: " + handshakeData.getHttpStatusMessage)
    }
  }

  try {
    client.connect()
    println("connected to: " + client.getURI)
  } catch {
    case e: Exception =>
      println("\n.....error in JWebSocketClient could not connect to: " + client.getURI)
      context.parent ! ConnectionFailed
      context stop self
  }

  // messages received by this actor
  def receive = {
    // send the msg to the server
    case Send(msg) => client.send(msg)

    case x => log.info("\nin JWebSocketClient received message: " + x.toString)
  }

}

object JWebSocketClient {
  def props(uris: String, handlerList: mutable.HashSet[ActorRef]): Props = Props(new JWebSocketClient(uris, handlerList))
}