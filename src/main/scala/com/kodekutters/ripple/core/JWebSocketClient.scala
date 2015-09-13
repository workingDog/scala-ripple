package com.kodekutters.ripple.core

import akka.actor._
import com.kodekutters.ripple.protocol.Response
import messages.{Disconnect, ResponseMsg, ConnectionFailed, Send}
import org.java_websocket.handshake.ServerHandshake
import play.api.libs.json.{JsError, JsSuccess, Json}
import scala.collection.mutable
import java.net.URI
import org.java_websocket.drafts.{Draft_10, Draft_17}
import collection.JavaConversions._
import java.util.Base64


/**
 * the web socket client that connects to the ripple server
 *
 * @param uris the server uri, e.g. "wss://s1.ripple.com:443/"
 * @param handlerList the list of responses handlers
 */

class JWebSocketClient(uris: String, handlerList: mutable.HashSet[ActorRef]) extends Actor with ActorLogging {

  import Response._

  //val headers: java.util.Map[String, String] = mutable.Map[String,String]()
  // Map(("Authorization", "Basic " + Base64.getUrlEncoder.encodeToString((user + ":" + pass).getBytes)) :: Nil: _*)

  // the client receives all server responses and pass them onto the handlers
  val client = new WSClient(uris) {
    // receive the server response messages
    override def onMessage(msg: String) = {
      try {
        // convert the msg to a Response, validating the message in the process
        Json.fromJson(Json.parse(msg)) match {
          // forward all valid responses to all handlers
          case response: JsSuccess[Response] => handlerList.foreach(handler => handler forward ResponseMsg(response.get))

          case e: JsError =>
            println("\n error... Response cannot be validated: " + JsError.toJson(e).toString())
            println("\n " + Json.prettyPrint(Json.parse(msg)))
        }
      } catch {
        case e: Exception => println("\n.....in JWebSocketClient error " + e)
      }
    }

    override def onError(ex: Exception) = {
      println("websockets onError: " + ex.toString)
    }

    override def onOpen(handshakeData: ServerHandshake) = {
      println("websockets connecting: " + handshakeData.getHttpStatusMessage)
    }
  }

  private def doConnect() = {
    try {
      client.connect()
      println("connected to: " + client.getURI)
    } catch {
      case e: Exception =>
        println("\n.....error in JWebSocketClient could not connect to: " + client.getURI)
        context.parent ! ConnectionFailed
        context stop self
    }
  }

  // start the connection
  doConnect()

  // messages received by this actor
  def receive = {
    // send the msg to the server
    case Send(msg) =>  client.send(msg)

    // close the websocket client
    case Disconnect => client.close()

    case x => log.info("\nin JWebSocketClient received message: " + x.toString)
  }

}

object JWebSocketClient {
  def props(uris: String, handlerList: mutable.HashSet[ActorRef]): Props = Props(new JWebSocketClient(uris, handlerList))
}