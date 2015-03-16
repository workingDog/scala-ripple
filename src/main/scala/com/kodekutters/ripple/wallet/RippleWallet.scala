package com.kodekutters.ripple.wallet

import com.kodekutters.ripple.protocol._
import play.api.libs.json._
import scala.collection.mutable


// just a test


final case class RippleWallet(secret_key: String) {

  var account_data: Option[Account_data] = None
  val trust_lines = new mutable.ListBuffer[Trust_line]()
  val offers = new mutable.ListBuffer[Offer]()
  val ious = new mutable.ListBuffer[Account_data]()
  val receive_currencies = new mutable.ListBuffer[String]()
  val send_currencies = new mutable.ListBuffer[String]()
  val transaction = new mutable.ListBuffer[Transaction]()

}

object RippleWallet {

  val rwReads: Reads[RippleWallet] = new Reads[RippleWallet] {
    def reads(json: JsValue) = Json.format[RippleWallet].reads(json)
  }

//(
  //  (JsPath \ "secret_key").read[String]
   //   (JsPath \ "account_data").read[Account_data] and
    //  (JsPath \ "trust_lines").read[mutable.ListBuffer[Trust_line]] and
    //  (JsPath \ "offers").read[mutable.ListBuffer[Offer]] and
    //  (JsPath \ "ious").read[mutable.ListBuffer[Account_data]]
   // )(RippleWallet.apply _)

  val rwWrites = new Writes[RippleWallet] {
    def writes(rw: RippleWallet) = Json.obj(
      "account_data" -> rw.account_data,
      "trust_lines" -> rw.trust_lines,
      "offers" -> rw.offers,
      "ious" -> rw.ious)
  }

  implicit val fmt: Format[RippleWallet] = Format(rwReads, rwWrites)
}

