package com.kodekutters.ripple.examples

import messages.{Send, Register}
import akka.actor.{Props, Actor, ActorSystem}
import com.kodekutters.ripple.core.RippleLinker
import play.api.libs.json.Json


/**
 * does not work anymore !!!
 *
 * test connecting to the bitcoin blockchain
 * see https://blockchain.info/api/api_websocket
 */
object BitcoinTest {

  def main(args: Array[String]) {

    implicit val system = ActorSystem("bitcoinsession")

    val testMsg = Json.parse("""{"op":"unconfirmed_sub"}""")

   // val testMsg2 = Json.parse("""{"op":"addr_sub", "addr":"1F1tAaz5x1HUXrCNLbtMDqcw6o5GNn4xqX"}""")

    val ping2 = Json.parse("""{"op":"ping_block"}""")
    val ping = Json.parse("""{"op": "unconfirmed_sub"}""")
      // Json.parse("""{"op":"ping_block"}""")

    // handler for the responses to this request
    val bcHandler = system.actorOf(BitcoinHandler.props())

    // the blockchain server linker
    val linker = system.actorOf(RippleLinker.props("ws://ws.blockchain.info/inv"))
    Thread.sleep(3000)

    // register the handler
    linker ! Register(bcHandler)
    // send the request
    linker ! Send(ping.toString)
  }

}

class BitcoinHandler() extends Actor {
  def receive = {
    case x => println("bitcoin: " + x.toString)
  }
}

object BitcoinHandler {
  def props(): Props = Props(new BitcoinHandler())
}

