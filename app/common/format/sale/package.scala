package common.format

import models.SaleDetails
import models.db._
import play.api.libs.functional.syntax._
import play.api.libs.json._

package object sale {
  implicit val soldItemWrites: Writes[SoldItem] = (
    (__ \ "id").writeNullable[SoldItemId] and
      (__ \ "sale").write[SaleId] and
      (__ \ "item").write[ItemId] and
      (__ \ "fraction").writeNullable[FractionId] and
      (__ \ "count").write[Int] and
      (__ \ "price").write[Float] and
      (__ \ "taxes").write[Float] and
      (__ \ "cost").write[Float]
    )(unlift(SoldItem.unapply))

  implicit val soldItemReads: Reads[SoldItem] = (
    (__ \ "id").readNullable[SoldItemId] and
    ((__ \ "sale").read[SaleId] orElse Reads.pure(SaleId(-1))) and
      (__ \ "item").read[ItemId] and
      (__ \ "fraction").readNullable[FractionId] and
      (__ \ "count").read[Int] and
      (__ \ "price").read[Float] and
      (__ \ "taxes").read[Float] and
      (__ \ "cost").read[Float]
    )(SoldItem.apply _)


  implicit val detailedSaleReads: Reads[SaleDetails] = (
    (__ \ "items").read[Seq[SoldItem]] and
      (__ \ "details").read[JsValue]
    )(SaleDetails.apply _)
}
