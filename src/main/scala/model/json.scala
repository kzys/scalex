package org.scalex
package model

import play.api.libs.functional.syntax._
import play.api.libs.json._

import org.scalex.util.ScalexJson._

private[scalex] object json {

  implicit val blockFormat = (
    (__ \ "txt").format[String] and
    (__ \ "html").format[Option[String]] 
  )(Block.apply, unlift(Block.unapply))

  // TODO - Does it really need "default"?
  implicit val commentFormat: OFormat[Comment] = (
    (__ \ "body").format[Block].default and
    (__ \ "summary").format[Option[Block]].default and
    (__ \ "see").format[List[Block]].default and
    (__ \ "result").format[Option[Block]].default and
    (__ \ "throws").format[Map[String, Block]] and
    (__ \ "valueParams").format[Map[String, Block]] and
    (__ \ "typeParams").format[Map[String, Block]] and
    (__ \ "version").format[Option[Block]].default and
    (__ \ "since").format[Option[Block]].default and
    (__ \ "todo").format[List[Block]].default and
    (__ \ "deprecated").format[Option[Block]].default and
    (__ \ "note").format[List[Block]].default and
    (__ \ "example").format[List[Block]].default and
    (__ \ "constructor").format[Option[Block]].default
  )(Comment.apply, unlift(Comment.unapply))
}
