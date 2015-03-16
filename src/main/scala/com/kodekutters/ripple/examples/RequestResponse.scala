package com.kodekutters.ripple.examples

import akka.actor.{Props, ActorLogging, Actor}
import com.kodekutters.ripple.core.LinkerApp
import com.kodekutters.ripple.protocol._
import messages.JsonMessage
import play.api.libs.json.{JsError, JsSuccess, Json}


/**
 * testing sending ripple commands (requests) and receiving the corresponding responses from the server
 */
object RequestResponse extends LinkerApp {

  def main(args: Array[String]) {

    // BitStamp account   "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"
    val theAccount = "rKQsDnGTkc3532Zp7pX6zaLZz48xJBqkJL"

    val account_lines = new Account_lines("account_lines", Some(123), theAccount, None, Some("current"), None, None, None)

    val account_info = new Account_info("account_info", Some(123), theAccount, Some(true), None, Some("validated"))

    val account_offers = new Account_offers("account_offers", Some(123), theAccount, None, Some("current"), None, None)

    val account_tx = new Account_tx("account_tx", Some(123), "r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59", None, Some(-1), Some(-1), Some(2), None, None, Some(false), Some(false))

    val ledger = new Ledger("ledger", Some(123), None, None, None, None, None, Some("current"))

    println("\nrequest: " + Json.prettyPrint(Json.toJson(ledger)))

    // the handler for the responses
    withHandler(system.actorOf(TestHandler.props(123)))

    // send the request
    sendRequest(Json.toJson(ledger).toString)
  }

}

/**
 * a test handler that receives the ripple server responses
 * @param accountId
 */
class TestHandler(accountId: Int) extends Actor with ActorLogging {

  import Response._

  def receive = {

    case JsonMessage(js) =>
      println("\nresponse_js: " + Json.prettyPrint(js))
      // convert the json message into a Response object
      Json.fromJson(js) match {
        case response: JsSuccess[Response] => println("\nresponse_obj: " + response.get + "\n")
        case e: JsError => println("Errors: " + JsError.toFlatJson(e).toString())
      }

    case _ => None
  }
}

object TestHandler {
  def props(accountId: Int): Props = Props(new TestHandler(accountId))
}