package com.kodekutters.ripple

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
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
  //----------------------supporting elements-----------------------------------------
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
                              limit_peer: String, no_ripple: Option[Boolean] = None, no_ripple_peer: Option[Boolean] = None,
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
              val js = Json.parse( s"""{"currency": "", "value": "$amount", "issuer": ""}""")
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

  // if put "XRP" for currency, put None for the issuer (default), i.e. do not specify the issuer
  final case class CurrencyOffer(currency: String = "", issuer: Option[String] = None)

  object CurrencyOffer {
    implicit val fmt = Json.format[CurrencyOffer]
  }

  /**
   * an offer object
   *
   * @param flags
   * @param seq
   * @param taker_gets
   * @param taker_pays
   */
  final case class Offer(flags: Option[Int] = None, seq: Option[Int] = None, taker_gets: Option[String] = None, taker_pays: Option[CurrencyAmount] = None)

  object Offer {
    implicit val fmt = Json.format[Offer]
  }

  final case class OfferExtended(flags: Option[Int] = None, seq: Option[Int] = None, taker_gets: Option[String] = None, taker_pays: Option[CurrencyAmount] = None,
                                 taker_gets_funded: Option[String] = None, taker_pays_funded: Option[String] = None, quality: Int)

  object OfferExtended {
    implicit val fmt = Json.format[OfferExtended]
  }

  // binary false
  final case class Transaction(ledger_index: Option[Int] = None, meta: Option[JsValue] = None, tx: Option[JsValue] = None,
                               tx_blob: Option[String] = None, validated: Option[Boolean] = None)

  object Transaction {
    implicit val fmt = Json.format[Transaction]
  }

  final case class Marker(ledger: Option[Int] = None, seq: Option[Int] = None)

  object Marker {
    implicit val fmt = Json.format[Marker]
  }

  final case class Ledger_data(accepted: Option[Boolean] = None, account_hash: Option[String] = None, close_time: Option[Int] = None,
                               close_time_human: Option[String] = None, close_time_resolution: Option[Int] = None, closed: Option[Boolean] = None,
                               ledger_hash: Option[String] = None, ledger_index: Option[String] = None, parent_hash: Option[String] = None,
                               total_coins: Option[String] = None, transaction_hash: Option[String] = None, validated: Option[Boolean] = None)

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
   * @param id (WebSocket only) ID provided in the request that prompted this response
   * @param account A unique identifier for the account, most commonly the account’s address.
   * @param strict (Optional, defaults to False) If set to True, then the account field will only accept a public key or account address.
   * @param ledger_hash (Optional) A 20-byte hex string for the ledger version to use.
   * @param ledger_index (Optional) The sequence number of the ledger to use, or a shortcut string to choose a ledger automatically.
   */
  final case class Account_info(override val id: Option[Int] = None, account: String, strict: Option[Boolean] = None,
                                ledger_hash: Option[String] = None, ledger_index: Option[String] = None) extends RequestType("account_info", id)

  object Account_info {

    val theReads: Reads[Account_info] = new Reads[Account_info] {
      def reads(json: JsValue): JsResult[Account_info] = (
      (__ \ 'id).readNullable[Int] and
      (__ \ 'account).read[String] and
      (__ \ 'strict).readNullable[Boolean] and
      (__ \ 'ledger_hash).readNullable[String] and
      (__ \ 'ledger_index).readNullable[String] and
      (__ \ 'command).read[String](Reads.pattern("""account_info""".r))
    )( (id, account, strict, ledger_hash, ledger_index, _) => Account_info(id, account, strict, ledger_hash, ledger_index)).reads(json)
    }

    val theWrites: Writes[Account_info] = (
        (JsPath \ "command").write[String] and
        (JsPath \ "id").writeNullable[Int] and
        (JsPath \ "account").write[String] and
        (JsPath \ "strict").writeNullable[Boolean] and
        (JsPath \ "ledger_hash").writeNullable[String] and
        (JsPath \ "ledger_index").writeNullable[String]
      )(s => (s.command, s.id, s.account, s.strict, s.ledger_hash, s.ledger_index))

    implicit val fmt: Format[Account_info] = Format(theReads, theWrites)
  }

  final case class Account_lines(override val id: Option[Int] = None, account: String,
                                 ledger_hash: Option[String] = None, ledger_index: Option[String] = None,
                                 peer: Option[String] = None, limit: Option[Int] = None, marker: Option[String] = None) extends RequestType("account_lines", id)

  object Account_lines {

    val theReads: Reads[Account_lines] = new Reads[Account_lines] {
      def reads(json: JsValue): JsResult[Account_lines] = (
            (__ \ 'command).read[String](Reads.pattern("""account_lines""".r)) and
            (__ \ 'id).readNullable[Int] and
            (__ \ 'account).read[String] and
            (__ \ 'ledger_hash).readNullable[String] and
            (__ \ 'ledger_index).readNullable[String] and
            (__ \ 'peer).readNullable[String] and
            (__ \ 'limit).readNullable[Int] and
            (__ \ 'marker).readNullable[String]
    )( (_, id, account, ledger_hash, ledger_index, peer, limit, marker) => Account_lines(id, account, ledger_hash, ledger_index,peer, limit, marker)).reads(json)
    }

    val theWrites: Writes[Account_lines] = (
        (JsPath \ "command").write[String] and
        (JsPath \ "id").writeNullable[Int] and
        (JsPath \ "account").write[String] and
        (JsPath \ "ledger_hash").writeNullable[String] and
        (JsPath \ "ledger_index").writeNullable[String] and
        (JsPath \ "peer").writeNullable[String] and
        (JsPath \ "limit").writeNullable[Int] and
        (JsPath \ "marker").writeNullable[String]
      )(s => (s.command, s.id, s.account, s.ledger_hash, s.ledger_index, s.peer, s.limit, s.marker))

    implicit val fmt: Format[Account_lines] = Format(theReads, theWrites)
  }

  final case class Account_offers(override val id: Option[Int] = None, account: String,
                                  ledger_hash: Option[String] = None, ledger_index: Option[String] = None,
                                  limit: Option[Int] = None, marker: Option[String] = None) extends RequestType("account_offers", id)

  object Account_offers {

    val theReads: Reads[Account_offers] = new Reads[Account_offers] {
      def reads(json: JsValue): JsResult[Account_offers] = (
            (__ \ 'command).read[String](Reads.pattern("""account_offers""".r)) and
            (__ \ 'id).readNullable[Int] and
            (__ \ 'account).read[String] and
            (__ \ 'ledger_hash).readNullable[String] and
            (__ \ 'ledger_index).readNullable[String] and
            (__ \ 'limit).readNullable[Int] and
            (__ \ 'marker).readNullable[String]
    )( (_, id, account, ledger_hash, ledger_index, limit, marker) => Account_offers(id, account, ledger_hash, ledger_index, limit, marker)).reads(json)
    }

    val theWrites: Writes[Account_offers] = (
        (JsPath \ "command").write[String] and
        (JsPath \ "id").writeNullable[Int] and
        (JsPath \ "account").write[String] and
        (JsPath \ "ledger_hash").writeNullable[String] and
        (JsPath \ "ledger_index").writeNullable[String] and
        (JsPath \ "limit").writeNullable[Int] and
        (JsPath \ "marker").writeNullable[String]
      )(s => (s.command, s.id, s.account, s.ledger_hash, s.ledger_index, s.limit, s.marker))

    implicit val fmt: Format[Account_offers] = Format(theReads, theWrites)
  }

  final case class Account_tx(override val id: Option[Int] = None, account: String,
                              ledger_hash: Option[String] = None, ledger_index_min: Option[Int] = None,
                              ledger_index_max: Option[Int] = None, ledger_index: Option[String] = None,
                              limit: Option[Int] = None, marker: Option[String] = None,
                              binary: Option[Boolean] = None, forward: Option[Boolean] = None) extends RequestType("account_tx", id)

  object Account_tx {

    val theReads: Reads[Account_tx] = new Reads[Account_tx] {
      def reads(json: JsValue): JsResult[Account_tx] = (
            (__ \ 'command).read[String](Reads.pattern("""Account_tx""".r)) and
            (__ \ 'id).readNullable[Int] and
            (__ \ 'account).read[String] and
            (__ \ 'ledger_hash).readNullable[String] and
            (__ \ 'ledger_index_min).readNullable[Int] and
            (__ \ 'ledger_index_max).readNullable[Int] and
            (__ \ 'ledger_index).readNullable[String] and
            (__ \ 'limit).readNullable[Int] and
            (__ \ 'marker).readNullable[String] and
            (__ \ 'binary).readNullable[Boolean] and
            (__ \ 'forward).readNullable[Boolean]
    )( (_, id, account, ledger_hash, ledger_index_min, ledger_index_max, ledger_index,  limit, marker, binary, forward) =>
    Account_tx(id, account, ledger_hash, ledger_index_min, ledger_index_max, ledger_index, limit, marker, binary, forward)).reads(json)
    }

    val theWrites: Writes[Account_tx] = (
        (JsPath \ "command").write[String] and
        (JsPath \ "id").writeNullable[Int] and
        (JsPath \ "account").write[String] and
        (JsPath \ "ledger_hash").writeNullable[String] and
        (JsPath \ "ledger_index_min").writeNullable[Int] and
        (JsPath \ "ledger_index_max").writeNullable[Int] and
        (JsPath \ "ledger_index").writeNullable[String] and
        (JsPath \ "limit").writeNullable[Int] and
        (JsPath \ "marker").writeNullable[String] and
        (JsPath \ "binary").writeNullable[Boolean] and
        (JsPath \ "forward").writeNullable[Boolean]
      )(s => (s.command, s.id, s.account, s.ledger_hash, s.ledger_index_min, s.ledger_index_max, s.ledger_index, s.limit, s.marker, s.binary, s.forward))

    implicit val fmt: Format[Account_tx] = Format(theReads, theWrites)
  }

  final case class Ledger(override val id: Option[Int] = None, accounts: Option[Boolean] = None,
                          transactions: Option[Boolean] = None, full: Option[Boolean] = None, expand: Option[Boolean] = None,
                          ledger_hash: Option[String] = None, ledger_index: Option[String] = None) extends RequestType("ledger", id)

  object Ledger {

    val theReads: Reads[Ledger] = new Reads[Ledger] {
      def reads(json: JsValue): JsResult[Ledger] = (
            (__ \ 'command).read[String](Reads.pattern("""ledger""".r)) and
            (__ \ 'id).readNullable[Int] and
            (__ \ 'accounts).readNullable[Boolean] and
            (__ \ 'transactions).readNullable[Boolean] and
            (__ \ 'full).readNullable[Boolean] and
            (__ \ 'expand).readNullable[Boolean] and
            (__ \ 'ledger_hash).readNullable[String] and
            (__ \ 'ledger_index).readNullable[String]
    )( (_, id, accounts, transactions, full, expand, ledger_hash, ledger_index) => Ledger(id, accounts, transactions, full, expand, ledger_hash, ledger_index)).reads(json)
    }

    val theWrites: Writes[Ledger] = (
        (JsPath \ "command").write[String] and
        (JsPath \ "id").writeNullable[Int] and
        (JsPath \ "accounts").writeNullable[Boolean] and
        (JsPath \ "transactions").writeNullable[Boolean] and
        (JsPath \ "full").writeNullable[Boolean] and
        (JsPath \ "expand").writeNullable[Boolean] and
        (JsPath \ "ledger_hash").writeNullable[String] and
        (JsPath \ "ledger_index").writeNullable[String]
      )(s => (s.command, s.id, s.accounts, s.transactions, s.full, s.expand, s.ledger_hash, s.ledger_index))

    implicit val fmt: Format[Ledger] = Format(theReads, theWrites)
  }

  final case class Book_offers(override val id: Option[Int] = None, limit: Option[Int] = None, taker: Option[String] = None,
                               taker_gets: CurrencyOffer, taker_pays: CurrencyAmount,
                               ledger_hash: Option[String] = None, ledger_index: Option[String] = None) extends RequestType("book_offers", id)

  object Book_offers {

    val theReads: Reads[Book_offers] = new Reads[Book_offers] {
      def reads(json: JsValue): JsResult[Book_offers] = (
            (__ \ 'command).read[String](Reads.pattern("""book_offers""".r)) and
            (__ \ 'id).readNullable[Int] and
            (__ \ 'limit).readNullable[Int] and
            (__ \ 'taker).readNullable[String] and
            (__ \ 'taker_gets).read[CurrencyOffer] and
            (__ \ 'taker_pays).read[CurrencyAmount] and
            (__ \ 'ledger_hash).readNullable[String] and
            (__ \ 'ledger_index).readNullable[String]
    )( (_, id, limit, taker, taker_gets, taker_pays, ledger_hash, ledger_index) => Book_offers(id, limit, taker, taker_gets, taker_pays,ledger_hash, ledger_index)).reads(json)
    }

    val theWrites: Writes[Book_offers] = (
        (JsPath \ "command").write[String] and
        (JsPath \ "id").writeNullable[Int] and
        (JsPath \ "limit").writeNullable[Int] and
        (JsPath \ "taker").writeNullable[String] and
        (JsPath \ "taker_gets").write[CurrencyOffer] and
        (JsPath \ "taker_pays").write[CurrencyAmount] and
        (JsPath \ "ledger_hash").writeNullable[String] and
        (JsPath \ "ledger_index").writeNullable[String]
      )(s => (s.command, s.id, s.limit, s.taker, s.taker_gets, s.taker_pays, s.ledger_hash, s.ledger_index))

    implicit val fmt: Format[Book_offers] = Format(theReads, theWrites)
  }

  class RequestType(val command: String, val id: Option[Int] = None)

  object RequestType {

    def apply(command: String, id: Option[Int] = None) = new RequestType(command, id)

    def unapply(req: RequestType) = if (req == null) None else Some(req.command, req.id)

    val theReads: Reads[RequestType] = new Reads[RequestType] {
      def reads(json: JsValue): JsResult[RequestType] = (
        (__ \ 'command).read[String] and
        (__ \ 'id).readNullable[Int]
    )( (command, id) => RequestType(command, id)).reads(json)
    }

    val theWrites: Writes[RequestType] = (
        (JsPath \ "command").write[String] and
        (JsPath \ "id").writeNullable[Int]
      )(s => (s.command, s.id))

    implicit val fmt: Format[RequestType] = Format(theReads, theWrites)

  }

  //-------------------------------------------------------------------------------
  //---------------------------responses-------------------------------------------
  //-------------------------------------------------------------------------------

  final case class Account_lines_response(account: String, lines: Array[Trust_line], ledger_current_index: Option[Int] = None,
                                          ledger_hash: Option[String] = None, markers: Option[String] = None,
                                          ledger_index: Option[Int] = None, validated: Option[Boolean] = None) extends ResponseType

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
  final case class Account_info_response(account_data: Account_data, ledger_index: Option[Int] = None, validated: Option[Boolean] = None) extends ResponseType

  object Account_info_response {
    implicit val fmt = Json.format[Account_info_response]
  }

  final case class Account_offers_response(account: String, offers: Array[Offer], ledger_current_index: Option[Int] = None,
                                           ledger_index: Option[String] = None, ledger_hash: Option[String] = None, marker: Option[String] = None) extends ResponseType

  object Account_offers_response {
    implicit val fmt = Json.format[Account_offers_response]
  }

  final case class Account_tx_response(account: String, ledger_index_min: Option[Int] = None,
                                       validated: Option[Boolean] = None, ledger_index_max: Option[Int] = None,
                                       limit: Option[Int] = None, marker: Option[Marker] = None, offset: Option[Int] = None,
                                       transactions: Array[Transaction]) extends ResponseType

  object Account_tx_response {
    implicit val fmt = Json.format[Account_tx_response]
  }

  final case class Book_offers_response(ledger_current_index: Option[Int] = None, ledger_index_min: Option[Int] = None,
                                        ledger_hash: Option[String] = None, offers: Array[OfferExtended]) extends ResponseType

  object Book_offers_response {
    implicit val fmt = Json.format[Book_offers_response]
  }

  final case class Ledger_response(ledger: Ledger_data, validated: Option[Boolean] = None,
                                   ledger_current_index: Option[Int] = None) extends ResponseType

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

    def toJsonString(resp: ResponseType) = Json.toJson(resp).toString

    // these reads are dangerous, it will match the first signature match and
    // that maybe not be the desired type.
    // todo must redo and check the signatures are different for all ResponseType
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
