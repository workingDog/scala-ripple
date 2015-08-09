package com.kodekutters.ripple.examples

import messages.{ResponseMsg, Send, Register}
import akka.actor.{Props, Actor, ActorSystem}
import com.kodekutters.ripple.core.RippleLinker
import play.api.libs.json.Json


/**
 * test connecting to the Stellar server
 * see https://www.stellar.org/api/
 */
object StellarTest {

  def main(args: Array[String]) {
    implicit val system = ActorSystem("stellartestsession")

    val test1 = Json.parse("""{ "command": "account_offers", "id": 1234567, "account": "ganVp9o5emfzpwrG5QVUXqMv8AgLcdvySb" }""")

    val test2 = Json.parse("""{ "command": "account_info", "id": 1234567, "account": "gM4Fpv2QuHY4knJsQyYGKEHFGw3eMBwc1U" }""")

    val ping = Json.parse("""{"command": "ping", "id": 1234567}""")

    // handler for the responses to this request
    val handler = system.actorOf(StellarHandler.props())

    // the Stellar server linker
    val linker = system.actorOf(RippleLinker.props("ws://live.stellar.org:9001"))
    Thread.sleep(2000) // give the linker time to connect

    // register the handler
    linker ! Register(handler)
    // send the request
    linker ! Send(test1.toString)
  }
}

/**
 * a test handler that receives the stellar server responses
 */
class StellarHandler() extends Actor {

  def receive = {

    case ResponseMsg(response) => println("\nStellarHandler response msg: \n" + response +
      "\n" + Json.prettyPrint(Json.toJson(response)))
      sys.exit()  // for testing

    case x => println("StellarHandler: " + x.toString)
  }
}

object StellarHandler {
  def props(): Props = Props(new StellarHandler())
}