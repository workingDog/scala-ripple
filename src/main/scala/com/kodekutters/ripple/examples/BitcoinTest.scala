package com.kodekutters.ripple.examples

import messages.{JsonMessage, Send, Register}
import akka.actor.{Props, Actor, ActorSystem}
import com.kodekutters.ripple.core.{RippleLinker}
import play.api.libs.json.Json


/**
 * test connecting to the bitcoin blockchain
 * see https://blockchain.info/api/api_websocket
 */
object BitcoinTest {

  def main(args: Array[String]) {
    implicit val system = ActorSystem("bitcoinsession")

    val testMsg = Json.parse( """{"op":"unconfirmed_sub"}""")

    //    val testMsg = Json.parse("""{"op":"addr_sub", "addr":"1F1tAaz5x1HUXrCNLbtMDqcw6o5GNn4xqX"}""")

    // handler for the responses to this request
    val bcHandler = system.actorOf(BitcoinHandler.props())

    // the blockchain server linker     ws://ws.blockchain.info:8335/inv
    val linker = system.actorOf(RippleLinker.props("wss://ws.blockchain.info/inv"))
    Thread.sleep(2000)

    // register the handler
    linker ! Register(bcHandler)
    // send the request
    linker ! Send(testMsg.toString)
  }

  class BitcoinHandler() extends Actor {
    def receive = {
      case JsonMessage(js) => println("\n js: " + Json.prettyPrint(js))
      case x => println("bitcoin: " + x.toString)
    }
  }

  object BitcoinHandler {
    def props(): Props = Props(new BitcoinHandler())
  }

}