package common.format

import models.{ListedProduct, ProductBrief, Product}
import models.db._
import play.api.libs.functional.syntax._
import play.api.libs.json._



package object products {
  implicit val brandWrites: Writes[Brand] = (
    (__ \ "id").writeNullable[BrandId] and
      (__ \ "name").write[String]
    )(unlift(Brand.unapply))

  implicit val brandReads: Reads[Brand] = (
    (__ \ "id").readNullable[BrandId] and
      (__ \ "name").read[String]
    )(Brand.apply _)

  implicit val tagWrites: Writes[models.db.Tag] = (
    (__ \ "id").writeNullable[TagId] and
      (__ \ "name").write[String]
    )(unlift(Tag.unapply))

  implicit val tagReads: Reads[models.db.Tag] = (
    (__ \ "id").readNullable[TagId] and
      (__ \ "name").read[String]
    )(Tag.apply _)

  implicit val taxWrites: Writes[Tax] = (
    (__ \ "id").write[Option[TaxId]] and
      (__ \ "name").write[String] and
      (__ \ "percentage").write[Float]
    )(unlift(Tax.unapply))

  implicit val taxReads: Reads[Tax] = (
    (__ \ "id").readNullable[TaxId] and
      (__ \ "name").read[String] and
      (__ \ "percentage").read[Float]
    )(Tax.apply _)

  implicit val fractionWrites: Writes[Fraction] = (
    (__ \ "id").write[Option[FractionId]] and
      (__ \ "item").write[Option[ItemId]] and
      (__ \ "name").write[String] and
      (__ \ "price").write[Double] and
      (__ \ "qty").write[Int]
    )(unlift(Fraction.unapply))

  implicit val fractionReads: Reads[Fraction] = (
    (__ \ "id").readNullable[FractionId] and
      (__ \ "item").readNullable[ItemId] and
      (__ \ "name").read[String] and
      (__ \ "price").read[Double] and
      (__ \ "qty").read[Int]
    )(Fraction.apply _)

  implicit val productReads: Reads[Product] = (
    (__ \ "id").readNullable[ItemId] and
      (__ \ "sku").read[String] and
      (__ \ "name").read[String] and
      (__ \ "brand").readNullable[Brand] and
      (__ \ "tags").read[Seq[Tag]] and
      (__ \ "cost").read[Double] and
      (__ \ "price").read[Double] and
      (__ \ "tax").readNullable[Tax] and
      (__ \ "retailPrice").read[Double] and
      (__ \ "trackStock").read[Boolean] and
      (__ \ "stockCount").read[Float] and
      (__ \ "alertStockLowLevel").read[Boolean] and
      (__ \ "stockAlertLevel").read[Float] and
      (__ \ "fractions").read[Seq[Fraction]]
    )(Product.apply _ )

  implicit val productWrites: Writes[Product] = (
    (__ \ "id").writeNullable[ItemId] and
      (__ \ "sku").write[String] and
      (__ \ "name").write[String] and
      (__ \ "brand").write[Option[Brand]] and
      (__ \ "tags").write[Seq[Tag]] and
      (__ \ "cost").write[Double] and
      (__ \ "price").write[Double] and
      (__ \ "tax").write[Option[Tax]] and
      (__ \ "retailPrice").write[Double] and
      (__ \ "trackStock").write[Boolean] and
      (__ \ "stockCount").write[Float] and
      (__ \ "alertStockLowLevel").write[Boolean] and
      (__ \ "stockAlertLevel").write[Float] and
      (__ \ "fractions").write[Seq[Fraction]]
    )(unlift(Product.unapply))

  implicit val productBriefReads: Reads[ProductBrief] = (
    (__ \ "id").read[ItemId] and
      (__ \ "name").read[String] and
      (__ \ "sku").read[String] and
      (__ \ "brand").readNullable[String] and
      (__ \ "tags").read[Seq[String]] and
      (__ \ "retailPrice").read[Double] and
      (__ \ "stockCount").read[Float]
    )(ProductBrief.apply _ )

  implicit val productBriefWrites: Writes[ProductBrief] = (
    (__ \ "id").write[ItemId] and
      (__ \ "name").write[String] and
      (__ \ "sku").write[String] and
      (__ \ "brand").write[Option[String]] and
      (__ \ "tags").write[Seq[String]] and
      (__ \ "retailPrice").write[Double] and
      (__ \ "stockCount").write[Float]
    )(unlift(ProductBrief.unapply))

  implicit val productBriefsResponse: Writes[Tuple2[Int, Seq[ProductBrief]]] = (
    (__ \ "total").write[Int] and
      (__ \ "briefs").write[Seq[ProductBrief]]
    )(unlift(Tuple2.unapply[Int, Seq[ProductBrief]]))

  implicit val listedProductWrites: Writes[ListedProduct] = (
    (__ \ "id").write[ItemId] and
      (__ \ "sku").write[String] and
      (__ \ "name").write[String]
    )(unlift(ListedProduct.unapply))
}
