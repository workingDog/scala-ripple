package com.kodekutters.ripple.examples

import akka.actor.{Props, ActorLogging, Actor}
import com.kodekutters.ripple.core.LinkerApp
import com.kodekutters.ripple.protocol._
import messages.ResponseMsg
import play.api.libs.json.Json


/**
 * tests sending ripple commands (requests) and receiving the corresponding responses from the server
 */
object RequestResponse extends LinkerApp {

  def main(args: Array[String]) {

    // BitStamp account   "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"
    val theAccount = "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"

    val orderBook = new Book_offers(Some(123), Some(10), Some("r9cZA1mLK5R5Am25ArfXFmqgNwjZgnfk59"),
      new CurrencyOffer(CurrencyAmount.XRP), new CurrencyAmount("1", "USD", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"))

    val account_lines = new Account_lines(theAccount)

    val account_info = new Account_info(123, theAccount, true, "", "validated")

    val account_offers = new Account_offers(Some(123), theAccount, None, Some("current"))

    val account_tx = new Account_tx(Some(123), theAccount, None, Some(-1), Some(-1), Some("current"), None, None, Some(false), Some(false))

    val ledger = new Ledger(Some(123), None, None, None, None, None, Some("current"))

    println("\nrequest: " + Json.prettyPrint(Json.toJson(account_info)))

    // the handler for the responses
    withHandler(system.actorOf(TestHandler.props(123)))

    // send the request
    sendRequest(Json.toJson(account_info).toString)

  }

}

/**
 * a test handler that receives the ripple server responses
 * @param id the id that can be used to identify the request/response
 */
class TestHandler(id: Int) extends Actor with ActorLogging {

  def receive = {

    case ResponseMsg(response) =>
      println("\nresponse msg: \n" + response + "\n" + Json.prettyPrint(Json.toJson(response)))

      println("response.result: \n" + response.result)

      val result = Json.toJson(response.result)
      println("\nresult: \n" + result)

      sys.exit() // for testing

    case _ => None
  }
}

object TestHandler {
  def props(id: Int): Props = Props(new TestHandler(id))
}