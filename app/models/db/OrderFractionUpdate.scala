package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import repositories._
import slick.lifted.{Tag => SlickTag}

case class OrderFractionUpdateId(id: Long) extends AnyVal with BaseId
object OrderFractionUpdateId extends IdCompanion[OrderFractionUpdateId]

case class OrderFractionUpdate( id: Option[OrderFractionUpdateId], orderItem: OrderItemId,
                                price: Double, oldPrice: Double ) extends WithId[OrderFractionUpdateId]

class OrderFractionUpdates(tag: SlickTag) extends IdTable[OrderFractionUpdateId, OrderFractionUpdate](tag, "order_fractions_updates") {
  override def * = (id.?, orderItem, price, oldPrice) <> (OrderFractionUpdate.tupled, OrderFractionUpdate.unapply)

  def orderItem = column[OrderItemId]("oder_item")
  def fraction = column[FractionId]("fraction")
  def price = column[Double]("price")
  def oldPrice = column[Double]("old_price")

  def orderItemIdIdx = index("idx_order_fraction_item", orderItem, unique = false)
  def orderFk = foreignKey("fk_order_fractiom_item", orderItem, orderItemsQuery)(_.id)
}
