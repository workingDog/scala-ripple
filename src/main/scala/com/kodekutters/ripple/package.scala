package com.kodekutters.ripple

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * the ripple protocol
 *
 * Public Commands; MANAGING ACCOUNTS,
 *
 * reference: https://ripple.com/build/rippled-apis/#websocket-and-json-rpc-apis
 *
 */
package object protocol {

  //-------------------------------------------------------------------------------
  //----------------------support elements-----------------------------------------
  //-------------------------------------------------------------------------------

  /**
   * Information about the requested account
   *
   * @param Account Address of the requested account
   * @param Balance XRP balance in “drops” represented as a string
   * @param Flags Integer with different bits representing the status of several account flags
   * @param LedgerEntryType “AccountRoot” is the type of ledger entry that holds an account’s data
   * @param OwnerCount Number of other ledger entries (specifically, trust lines and offers) attributed to this account.
   *                   This is used to calculate the total reserve required to use the account.
   * @param PreviousTxnID Hash value representing the most recent transaction that affected this account
   * @param PreviousTxnLgrSeq Hash value representing the most recent transaction that affected this account
   * @param Sequence The sequence number of the next valid transaction for this account.
   *                 (Each account starts with Sequence = 1 and increases each time a transaction is made.)
   * @param index A unique index for the AccountRoot node that represents this account in the ledger.
   */
  final case class Account_data(Account: String, Balance: String, Flags: Int, LedgerEntryType: String, OwnerCount: Int,
                                PreviousTxnID: String, PreviousTxnLgrSeq: Int, Sequence: Int, index: String)

  object Account_data {
    implicit val fmt = Json.format[Account_data]
  }

  // quality_in and quality_out should be unsigned
  final case class Trust_line(account: String, balance: String, currency: String, limit: String,
                              limit_peer: String, no_ripple: Option[Boolean], no_ripple_peer: Option[Boolean],
                              quality_in: Int, quality_out: Int)

  object Trust_line {
    implicit val fmt = Json.format[Trust_line]
  }

  final case class CurrencyAmount(currency: String, value: String, issuer: String)

  object CurrencyAmount {
    implicit val fmt = Json.format[CurrencyAmount]
  }

  final case class Offer(flags: Option[Int], seq: Option[Int], taker_gets: Option[String], taker_pays: Option[CurrencyAmount])

  object Offer {
    implicit val fmt = Json.format[Offer]
  }

  // binary false
  final case class Transaction(ledger_index: Option[Int], meta: Option[JsValue], tx: Option[JsValue],
                               tx_blob: Option[String], validated: Option[Boolean])

  object Transaction {
    implicit val fmt = Json.format[Transaction]
  }

  final case class Marker(ledger: Option[Int], seq: Option[Int])

  object Marker {
    implicit val fmt = Json.format[Marker]
  }

  //-------------------------------------------------------------------------------
  //--------------------------requests/commands------------------------------------
  //-------------------------------------------------------------------------------

  /**
   * The account_info command retrieves information about an account, its activity, and its XRP balance.
   * All information retrieved is relative to a particular version of the ledger.
   *
   * @param command the WebSocket request command name, must be "account_info"
   * @param id (WebSocket only) ID provided in the request that prompted this response
   * @param account A unique identifier for the account, most commonly the account’s address.
   * @param strict (Optional, defaults to False) If set to True, then the account field will only accept a public key or account address.
   * @param ledger_hash (Optional) A 20-byte hex string for the ledger version to use.
   * @param ledger_index (Optional) The sequence number of the ledger to use, or a shortcut string to choose a ledger automatically.
   */
  final case class Account_info(command: String = "account_info", id: Option[Int], account: String,
                                strict: Option[Boolean], ledger_hash: Option[String],ledger_index: Option[String]) extends RequestType

  object Account_info {
    implicit val fmt = Json.format[Account_info]
  }

  final case class Account_lines(command: String = "account_lines", id: Option[Int], account: String,
                                 ledger_hash: Option[String], ledger_index: Option[String],
                                 peer: Option[String], limit: Option[Int], marker: Option[String]) extends RequestType

  object Account_lines {
    implicit val fmt = Json.format[Account_lines]
  }

  final case class Account_offers(command: String = "account_offers", id: Option[Int], account: String,
                                  ledger_hash: Option[String], ledger_index: Option[String],
                                  limit: Option[Int], marker: Option[String]) extends RequestType

  object Account_offers {
    implicit val fmt = Json.format[Account_offers]
  }

  final case class Account_tx(command: String = "account_tx", id: Option[Int], account: String,
                              ledger_hash: Option[String], ledger_index_min: Option[Int],
                              ledger_index_max: Option[Int], limit: Option[Int],
                              ledger_index: Option[String], marker: Option[String],
                              binary: Option[Boolean], forward: Option[Boolean]) extends RequestType

  object Account_tx {
    implicit val fmt = Json.format[Account_tx]
  }

  sealed trait RequestType {
    val command: String
    val id: Option[Int]
  }

  object RequestType {

    val requestTypeReads = new Reads[RequestType] {
      def reads(json: JsValue) = {
        (json \ "command").asOpt[String] match {
          case None => JsError("in requestTypeReads could not read the command: " + json + " into a RequestType")
          case Some(cmd) =>
            cmd match {
              case "account_info" => Json.format[Account_info].reads(json)
              case "account_lines" => Json.format[Account_lines].reads(json)
              case "account_offers" => Json.format[Account_offers].reads(json)
              case "account_tx" => Json.format[Account_tx].reads(json)
              case _ => JsError("in requestTypeReads could not read: " + json + " into a RequestType")
            }
        }
      }
    }

    val requestTypeWrites = new Writes[RequestType] {
      def writes(requestType: RequestType) =
        requestType match {
          case x: Account_info => Json.format[Account_info].writes(x)
          case x: Account_lines => Json.format[Account_lines].writes(x)
          case x: Account_offers => Json.format[Account_offers].writes(x)
          case x: Account_tx => Json.format[Account_tx].writes(x)
          case _ => JsNull
        }
    }

    implicit val fmt: Format[RequestType] = Format(requestTypeReads, requestTypeWrites)
  }

  //-------------------------------------------------------------------------------
  //---------------------------responses-------------------------------------------
  //-------------------------------------------------------------------------------

  final case class Account_lines_response(account: String, lines: Array[Trust_line], ledger_current_index: Option[Int],
                                          ledger_hash: Option[String], markers: Option[String],
                                          ledger_index: Option[Int], validated: Option[Boolean]) extends ResponseType

  object Account_lines_response {
    implicit val fmt = Json.format[Account_lines_response]
  }

  /**
   * response to a Account_info request
   * 
   * @param account_data Information about the requested account
   * @param ledger_index (Omitted if ledger_current_index is provided instead)
   *                     The sequence number of the ledger used when retrieving this information.
   *                     The information does not contain any changes from ledgers newer than this one.
   * @param validated True if this data is from a validated ledger version;
   *                  if omitted or set to false, this data is not final.
   */
  final case class Account_info_response(account_data: Account_data, ledger_index: Option[Int], validated: Option[Boolean]) extends ResponseType

  object Account_info_response {
    implicit val fmt = Json.format[Account_info_response]
  }

  final case class Account_offers_response(account: String, offers: Array[Offer], ledger_current_index: Option[Int],
                                           ledger_index: Option[String], ledger_hash: Option[String], marker: Option[String]) extends ResponseType

  object Account_offers_response {
    implicit val fmt = Json.format[Account_offers_response]
  }

  final case class Account_tx_response(account: String, ledger_index_min: Option[Int],
                                       validated: Option[Boolean], ledger_index_max: Option[Int],
                                       limit: Option[Int], marker: Option[Marker], offset: Option[Int],
                                       transactions: Array[Transaction]) extends ResponseType

  object Account_tx_response {
    implicit val fmt = Json.format[Account_tx_response]
  }

  sealed trait ResponseType

  object ResponseType {

    // these reads are dangerous, it will match the first pattern maybe not the correct type
    val responseTypeReads =
      JsPath.read[Account_info_response].map(x => x: ResponseType) |
        JsPath.read[Account_lines_response].map(x => x: ResponseType) |
        JsPath.read[Account_offers_response].map(x => x: ResponseType) |
        JsPath.read[Account_tx_response].map(x => x: ResponseType)

    // these reads are dangerous, it will match the first pattern maybe not the correct type
//    val responseTypeReads2 = new Reads[ResponseType] {
//      def reads(json: JsValue) = {
//        json match {
//          case x if json.validate[Account_info_response].isSuccess => Json.format[Account_info_response].reads(json)
//          case x if json.validate[Account_lines_response].isSuccess => Json.format[Account_lines_response].reads(json)
//          case x if json.validate[Account_offers_response].isSuccess => Json.format[Account_offers_response].reads(json)
//          case x if json.validate[Account_tx_response].isSuccess => Json.format[Account_tx_response].reads(json)
//          case _ => JsError("responseTypeReads could not read jsValue: " + json + " into a ResponseType")
//        }
//      }
//    }

    val responseTypeWrites = new Writes[ResponseType] {
      def writes(responseType: ResponseType) =
        responseType match {
          case x: Account_info_response => Json.format[Account_info_response].writes(x)
          case x: Account_lines_response => Json.format[Account_lines_response].writes(x)
          case x: Account_offers_response => Json.format[Account_offers_response].writes(x)
          case x: Account_tx_response => Json.format[Account_tx_response].writes(x)
          case _ => JsNull
        }
    }

    implicit val fmt: Format[ResponseType] = Format(responseTypeReads, responseTypeWrites)
  }

  // NOTE: id can be a string or int or ...
  case class Response(`type`: Option[String], id: Option[Int], status: Option[String],
                      result: Option[ResponseType], error: Option[String], error_code: Option[Int],
                      error_message: Option[String], request: Option[String])

  object Response {

    val responseReads: Reads[Response] = (
      (JsPath \ "type").readNullable[String] and
        (JsPath \ "id").readNullable[Int] and
        (JsPath \ "status").readNullable[String] and
        (JsPath \ "result").readNullable[ResponseType] and
        (JsPath \ "error").readNullable[String] and
        (JsPath \ "error_code").readNullable[Int] and
        (JsPath \ "error_message").readNullable[String] and
        (JsPath \ "request").readNullable[String]
      )(Response.apply _)

    val responseWrites = new Writes[Response] {
      def writes(response: Response) = Json.format[Response].writes(response)
    }

    implicit val fmt: Format[Response] = Format(responseReads, responseWrites)
  }

}
