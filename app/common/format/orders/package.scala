package common.format

import java.time.LocalDateTime

import models.db._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, _}

package object orders {
  implicit val orderWrites: Writes[Order] = (
    (__ \ "id").write[Option[OrderId]] and
      (__ \ "status").write[Int] and
      (__ \ "supplier").write[String] and
      (__ \ "invoice").writeNullable[String] and
      (__ \ "createdDateTime").write[LocalDateTime] and
      (__ \ "receivedDateTime").writeNullable[LocalDateTime]
    ) (unlift(Order.unapply))

  val fractionsDetailWriter = new Writes[String] {
    override def writes(o: String): JsValue = Json.parse(o)
  }

  implicit val orderItemWrites: Writes[OrderItem] = (
    (__ \ "id").writeNullable[OrderItemId] and
    (__ \ "order").write[OrderId] and
    (__ \ "item").write[ItemId] and
    (__ \ "count").write[Double] and
    (__ \ "cost").write[Double] and
    (__ \ "price").write[Double] and
    (__ \ "retailPrice").write[Double] and
    (__ \ "oldCost").write[Double] and
    (__ \ "oldPrice").write[Double] and
    (__ \ "oldRetailPrice").write[Double] and
    (__ \ "fractionsDetail").writeNullable[String](fractionsDetailWriter)
    )(unlift(OrderItem.unapply))

  implicit val detailedOrderWrites: Writes[DetailedOrder] = (
    (__ \ "order").write[Order] and
    (__ \ "items").write[Seq[OrderItem]]
    )(unlift(DetailedOrder.unapply))
}
