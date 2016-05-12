package common.format

import models.SaleDetails
import models.db._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import processors.ResponseStatus.ResponseStatus
import processors.{SimpleSalesProcessorResponse, SalesProcessorResponse}

package object sale {
  implicit val soldItemWrites: Writes[SoldItem] = (
    (__ \ "id").writeNullable[SoldItemId] and
      (__ \ "sale").write[SaleId] and
      (__ \ "item").write[ItemId] and
      (__ \ "fraction").writeNullable[FractionId] and
      (__ \ "count").write[Int] and
      (__ \ "price").write[Double] and
      (__ \ "taxes").write[Double] and
      (__ \ "cost").write[Double]
    )(unlift(SoldItem.unapply))

  implicit val soldItemReads: Reads[SoldItem] = (
    (__ \ "id").readNullable[SoldItemId] and
    ((__ \ "sale").read[SaleId] orElse Reads.pure(SaleId(-1))) and
      (__ \ "item").read[ItemId] and
      (__ \ "fraction").readNullable[FractionId] and
      (__ \ "count").read[Int] and
      (__ \ "price").read[Double] and
      (__ \ "taxes").read[Double] and
      (__ \ "cost").read[Double]
    )(SoldItem.apply _)


  implicit val detailedSaleReads: Reads[SaleDetails] = (
    (__ \ "items").read[Seq[SoldItem]] and
      (__ \ "details").read[JsValue]
    )(SaleDetails.apply _)

  implicit val simpleSalesProcessorResponseWrites: Writes[SimpleSalesProcessorResponse] = (
    (__ \ "status").write[ResponseStatus] and
      (__ \ "error").writeNullable[String] and
      (__ \ "invoiceId").writeNullable[Long]
    )(unlift(SimpleSalesProcessorResponse.unapply))

  implicit object salesProcessorResponseWrites extends Writes[SalesProcessorResponse] {
    override def writes(o: SalesProcessorResponse): JsValue = o match {
      case r : SimpleSalesProcessorResponse => Json.toJson(r)(simpleSalesProcessorResponseWrites)
      case x => throw new RuntimeException(s"Unknown SalesProcessorResponse: $x")
    }
  }
}