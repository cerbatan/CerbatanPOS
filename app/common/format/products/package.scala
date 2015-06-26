package common.format

import models.db.{Brand, BrandId, Tag, TagId}
import play.api.libs.functional.syntax._
import play.api.libs.json._

package object products {
  implicit val brandWrites: Writes[Brand] = (
    (__ \ "id").write[Option[BrandId]] and
      (__ \ "name").write[String]
    )(unlift(Brand.unapply))

  implicit val tagWrites: Writes[Tag] = (
    (__ \ "id").write[Option[TagId]] and
      (__ \ "name").write[String]
    )(unlift(Tag.unapply))
}
