package com.kodekutters.ripple.examples

import messages.{JsonMessage, Send, Register}
import akka.actor.{Props, Actor, ActorSystem}
import com.kodekutters.ripple.core.{RippleLinker}
import play.api.libs.json.Json


/**
 * test connecting to the Stellar server
 * see https://www.stellar.org/api/
 */
object StellarTest {

  def main(args: Array[String]) {
    implicit val system = ActorSystem("stellartestsession")

    val testMsg = Json.parse("""{"command":"subscribe","id":2,"streams":["ledger"]}""")

    // handler for the responses to this request
    val handler = system.actorOf(StellarHandler.props())

    // the Stellar server linker
    val linker = system.actorOf(RippleLinker.props("ws://live.stellar.org:9001"))
    Thread.sleep(2000) // give the linker time to connect

    // register the handler
    linker ! Register(handler)
    // send the request
    linker ! Send(testMsg.toString)
  }
}

/**
 * a test handler that receives the stellar server responses
 */
class StellarHandler() extends Actor {

  def receive = {

    case JsonMessage(js) => println("\n StellarHandler js: " + Json.prettyPrint(js))

    case x => println("StellarHandler: " + x.toString)
  }
}

object StellarHandler {
  def props(): Props = Props(new StellarHandler())
}