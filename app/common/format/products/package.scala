package common.format

import models.db._
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

  implicit val taxWrites: Writes[Tax] = (
    (__ \ "id").write[Option[TaxId]] and
      (__ \ "name").write[String] and
      (__ \ "percentage").write[Float]
    )(unlift(Tax.unapply))

  implicit val taxReads: Reads[Tax] = (
    (__ \ "id").read[Option[TaxId]] and
      (__ \ "name").read[String] and
      (__ \ "percentage").read[Float]
    )(Tax.apply _)
}
