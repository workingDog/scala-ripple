
import akka.actor.ActorRef
import play.api.libs.json._

/**
 * the messages used by the actors.
 */
package object messages {

  case object ConnectionFailed

  case object CloseAll

  case object CloseHandler

  case class Close(handler: ActorRef)

  case class Register(handler: ActorRef)

  case class DeRegister(handler: ActorRef)

  case class Send(message: String)

  case class JsonMessage(js: JsValue)

}
