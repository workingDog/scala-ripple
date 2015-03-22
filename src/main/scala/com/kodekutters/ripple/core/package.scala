
import akka.actor.ActorRef
import com.kodekutters.ripple.protocol.Response

/**
 * the messages used by the actors.
 */
package object messages {

  case object ConnectionFailed

  case object CloseAll

  case object CloseHandler

  case object Disconnect

  case class Close(handler: ActorRef)

  case class Register(handler: ActorRef)

  case class DeRegister(handler: ActorRef)

  case class Send(message: String)

  case class ResponseMsg(response: Response)

}
