package common.format

import models.Product
import models.db._
import play.api.libs.functional.syntax._
import play.api.libs.json._


package object products {
  implicit val brandWrites: Writes[Brand] = (
    (__ \ "id").write[Option[BrandId]] and
      (__ \ "name").write[String]
    )(unlift(Brand.unapply))

  implicit val brandReads: Reads[Brand] = (
    (__ \ "id").read[Option[BrandId]] and
      (__ \ "name").read[String]
    )(Brand.apply _)

  implicit val tagWrites: Writes[Tag] = (
    (__ \ "id").write[Option[TagId]] and
      (__ \ "name").write[String]
    )(unlift(Tag.unapply))

  implicit val tagReads: Reads[Tag] = (
    (__ \ "id").read[Option[TagId]] and
      (__ \ "name").read[String]
    )(Tag.apply _)

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

  implicit val fractionWrites: Writes[Fraction] = (
    (__ \ "id").write[Option[FractionId]] and
      (__ \ "item").write[Option[ItemId]] and
      (__ \ "sku").write[String] and
      (__ \ "name").write[String] and
      (__ \ "price").write[Double] and
      (__ \ "qty").write[Float]
    )(unlift(Fraction.unapply))

  implicit val fractionReads: Reads[Fraction] = (
    (__ \ "id").read[Option[FractionId]] and
      (__ \ "item").read[Option[ItemId]] and
      (__ \ "sku").read[String] and
      (__ \ "name").read[String] and
      (__ \ "price").read[Double] and
      (__ \ "qty").read[Float]
    )(Fraction.apply _)

  implicit val productReads: Reads[Product] = (
    (__ \ "id").read[Option[ItemId]] and
      (__ \ "sku").read[String] and
      (__ \ "name").read[String] and
      (__ \ "brand").read[Option[Brand]] and
      (__ \ "tags").read[List[Tag]] and
      (__ \ "cost").read[Double] and
      (__ \ "price").read[Double] and
      (__ \ "tax").read[Option[Tax]] and
      (__ \ "retailPrice").read[Double] and
      (__ \ "trackStock").read[Boolean] and
      (__ \ "stockCount").read[Float] and
      (__ \ "alertStockLowLevel").read[Boolean] and
      (__ \ "stockAlertLevel").read[Float] and
      (__ \ "fractions").read[List[Fraction]]
    )(Product.apply _ )

  implicit val productWrites: Writes[Product] = (
    (__ \ "id").write[Option[ItemId]] and
      (__ \ "sku").write[String] and
      (__ \ "name").write[String] and
      (__ \ "brand").write[Option[Brand]] and
      (__ \ "tags").write[List[Tag]] and
      (__ \ "cost").write[Double] and
      (__ \ "price").write[Double] and
      (__ \ "tax").write[Option[Tax]] and
      (__ \ "retailPrice").write[Double] and
      (__ \ "trackStock").write[Boolean] and
      (__ \ "stockCount").write[Float] and
      (__ \ "alertStockLowLevel").write[Boolean] and
      (__ \ "stockAlertLevel").write[Float] and
      (__ \ "fractions").write[List[Fraction]]
    )(unlift(Product.unapply))
}
