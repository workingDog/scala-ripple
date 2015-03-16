package com.kodekutters.ripple

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import scala.math.BigDecimal

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
  /**
   * a trust line object
   *
   * @param account The unique address of the account this line applies to.
   * @param balance Representation of the numeric balance currently held against this line. A positive balance means that the account holds value; a negative balance means that the account owes value.
   * @param currency The currency this line applies to
   * @param limit The maximum amount of the given currency that the account is willing to owe the peer account
   * @param limit_peer The maximum amount of currency that the peer account is willing to owe the account
   * @param no_ripple Whether or not the account has the NoRipple flag set for this line
   * @param no_ripple_peer Whether or not the peer account has the NoRipple flag set for the other direction of this trust line
   * @param quality_in Ratio for incoming transit fees represented in billionths. (For example, a value of 500 million represents a 0.5:1 ratio.) As a special case, 0 is treated as a 1:1 ratio.
   * @param quality_out Ratio for outgoing transit fees represented in billionths. (For example, a value of 500 million represents a 0.5:1 ratio.) As a special case, 0 is treated as a 1:1 ratio.
   */
  final case class Trust_line(account: String, balance: String, currency: String, limit: String,
                              limit_peer: String, no_ripple: Option[Boolean], no_ripple_peer: Option[Boolean],
                              quality_in: Int, quality_out: Int)

  object Trust_line {
    implicit val fmt = Json.format[Trust_line]
  }

  /**
   * a currency specification object
   *
   * @param currency Three-letter ISO 4217 Currency Code string (“XRP” is invalid). Alternatively, an unsigned 160-bit hex value according to the Currency format.
   * @param value Quoted decimal representation of the amount of currency
   * @param issuer Unique account address of the entity issuing the currency. In other words, the person or business where the currency can be redeemed.
   */
  final case class CurrencyAmount(value: String, currency: String = "", issuer: String = "") {

    def this(value: BigDecimal, currency: String, issuer: String) = this(value.toString, currency, issuer)

    // for XRP only
    def this(value: BigDecimal) = this(value.toString)

    def this(value: Int) = this(new BigDecimal(new java.math.BigDecimal(value)), "", "")

    def this(value: Float) = this(new BigDecimal(new java.math.BigDecimal(value)), "", "")

    def this(value: Long) = this(new BigDecimal(new java.math.BigDecimal(value)), "", "")
  }

  object CurrencyAmount {

    val XRP = "XRP"

    val currencyReads: Reads[CurrencyAmount] = new Reads[CurrencyAmount] {
      def reads(json: JsValue) = {
        val currency = (json \ "currency").asOpt[String]
        val issuer = (json \ "issuer").asOpt[String]

        if (currency.isDefined && issuer.isDefined) {
          Json.format[CurrencyAmount].reads(json)
        } else {
          json.asOpt[String] match {
            case None => Json.format[CurrencyAmount].reads(JsNull)
            case Some(amount) =>
              val js = Json.parse(s"""{"currency": "", "value": "$amount", "issuer": ""}""")
              Json.format[CurrencyAmount].reads(js)
          }
        }
      }
    }

    val currencyWrites = new Writes[CurrencyAmount] {
      def writes(w: CurrencyAmount) =
        if (w.currency.isEmpty && w.issuer.isEmpty) JsString(w.value) // XRP
        else Json.obj("currency" -> w.currency, "value" -> w.value, "issuer" -> w.issuer)
    }

    implicit val fmt: Format[CurrencyAmount] = Format(currencyReads, currencyWrites)
  }

  /**
   * an offer object
   *
   * @param flags
   * @param seq
   * @param taker_gets
   * @param taker_pays
   */
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

  final case class Ledger_data(accepted: Option[Boolean], account_hash: Option[String], close_time: Option[Int],
                               close_time_human: Option[String], close_time_resolution: Option[Int], closed: Option[Boolean],
                               ledger_hash: Option[String], ledger_index: Option[String], parent_hash: Option[String],
                               total_coins: Option[String], transaction_hash: Option[String], validated: Option[Boolean])

  object Ledger_data {
    implicit val fmt = Json.format[Ledger_data]
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
                                strict: Option[Boolean], ledger_hash: Option[String], ledger_index: Option[String]) extends RequestType
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

  final case class Ledger(command: String = "ledger", id: Option[Int], accounts: Option[Boolean] = None,
                          transactions: Option[Boolean] = None, full: Option[Boolean] = None, expand: Option[Boolean] = None,
                          ledger_hash: Option[String], ledger_index: Option[String]) extends RequestType
  object Ledger {
    implicit val fmt = Json.format[Ledger]
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
              case "ledger" => Json.format[Ledger].reads(json)
              case _ => JsError("in requestTypeReads could not read: " + json + " into a RequestType")
            }
        }
      }
    }

    val requestTypeWrites = Writes[RequestType] {
          case x: Account_info => Json.format[Account_info].writes(x)
          case x: Account_lines => Json.format[Account_lines].writes(x)
          case x: Account_offers => Json.format[Account_offers].writes(x)
          case x: Account_tx => Json.format[Account_tx].writes(x)
          case x: Ledger => Json.format[Ledger].writes(x)
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

  final case class Ledger_response(ledger: Ledger_data, validated: Option[Boolean],
                                   ledger_current_index: Option[Int]) extends ResponseType

  object Ledger_response {
    implicit val fmt = Json.format[Ledger_response]
  }

  // a catch all json result
  final case class PlainJson(value: JsValue) extends ResponseType

  object PlainJson {

    val gReads = JsPath.read[JsValue].map(s => PlainJson(s))

    val gWrites = new Writes[ResponseType] {
      def writes(value: ResponseType): JsValue = Json.obj("value" -> value)
    }

    implicit val fmt: Format[PlainJson] = Format(gReads, gWrites)
  }

  sealed trait ResponseType

  object ResponseType {

    // these reads are dangerous, it will match the first signature match and
    // that maybe not be the desired type.
    // todo must check the signatures are different for all ResponseType
    val responseTypeReads =
      JsPath.read[Account_info_response].map(x => x: ResponseType) |
        JsPath.read[Account_lines_response].map(x => x: ResponseType) |
        JsPath.read[Account_offers_response].map(x => x: ResponseType) |
        JsPath.read[Account_tx_response].map(x => x: ResponseType) |
        JsPath.read[Ledger_response].map(x => x: ResponseType) |
        JsPath.read[PlainJson].map(x => x: ResponseType)

    val responseTypeWrites = Writes[ResponseType] {
          case x: Account_info_response => Json.format[Account_info_response].writes(x)
          case x: Account_lines_response => Json.format[Account_lines_response].writes(x)
          case x: Account_offers_response => Json.format[Account_offers_response].writes(x)
          case x: Account_tx_response => Json.format[Account_tx_response].writes(x)
          case x: Ledger_response => Json.format[Ledger_response].writes(x)
          case x: PlainJson => Json.format[PlainJson].writes(x)
    }

    implicit val fmt: Format[ResponseType] = Format(responseTypeReads, responseTypeWrites)
  }

  // NOTE: id can be a string or int or ...
  /**
   * a response (from the server) object
   *
   * @param `type` Typically "response", which indicates a successful response to a command.
   * @param id (WebSocket only) ID provided in the request that prompted this response
   * @param status "success" if the request successfully completed. In the WebSocket API responses, this is included at the top level; in JSON-RPC and Commandline responses, this is included as a sub-field of the "result" object.
   * @param result The result of the query; contents vary depending on the command.
   * @param error A string description of the error
   * @param error_code A unique integer code for the type of error that occurred
   * @param error_message A string error message
   * @param request A copy of the request that prompted this error, in JSON format. Caution: If the request contained any account secrets, they are copied here!
   */
  case class Response(`type`: Option[String], id: Option[Int], status: Option[String],
                      result: Option[ResponseType], error: Option[String], error_code: Option[Int],
                      error_message: Option[String], request: Option[JsValue])

  object Response {

    val responseReads: Reads[Response] = (
      (JsPath \ "type").readNullable[String] and
        (JsPath \ "id").readNullable[Int] and
        (JsPath \ "status").readNullable[String] and
        (JsPath \ "result").readNullable[ResponseType] and
        (JsPath \ "error").readNullable[String] and
        (JsPath \ "error_code").readNullable[Int] and
        (JsPath \ "error_message").readNullable[String] and
        (JsPath \ "request").readNullable[JsValue]
      )(Response.apply _)

    val responseWrites = new Writes[Response] {
      def writes(response: Response) = Json.format[Response].writes(response)
    }

    implicit val fmt: Format[Response] = Format(responseReads, responseWrites)
  }

}
