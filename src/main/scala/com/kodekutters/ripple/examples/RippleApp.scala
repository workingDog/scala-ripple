package com.kodekutters.ripple.examples

import akka.actor.{Actor, Props}
import com.kodekutters.ripple.core.LinkerApp
import messages.ResponseMsg
import play.api.libs.json.Json

/**
 * an app test
 */

object RippleApp extends LinkerApp {

  def main(args: Array[String]) {
    // a string request
    val testMsg = Json.parse("""{"command":"subscribe","id":2,"streams":["ledger"]}""")
    // the handler for the responses
    withHandler(system.actorOf(Props(new MyHandler())))
    // send the requests
    sendRequest(testMsg.toString)
  }

  // responses to the requests are received here
  class MyHandler extends Actor {
    def receive = {
      case ResponseMsg(response) => println("\n response msg: " + response)
      case _ => None
    }
  }

}

