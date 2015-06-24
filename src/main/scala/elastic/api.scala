package org.scalex
package elastic

import akka.actor.ActorRef
import play.api.libs.json.JsObject

private[scalex] object api {

  case class Clear(typeName: Type, mapping: List[Any])

  case class Optimize(typeName: Type)

  case class IndexMany(typeName: Type, docs: List[(String, JsObject)])

  case class ThenDo(f: ActorRef ⇒ Fu[_])

}
