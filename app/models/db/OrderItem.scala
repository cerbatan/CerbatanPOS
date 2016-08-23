package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import repositories._
import slick.lifted.{Tag => SlickTag}

case class OrderItemId(id: Long) extends AnyVal with BaseId
object OrderItemId extends IdCompanion[OrderItemId]

case class OrderItem(id: Option[OrderItemId], order: OrderId, item: ItemId,
                     count: Double, cost: Double, price: Double, retailPrice: Double,
                     oldCost: Double, oldPrice: Double, oldRetailPrice: Double, fractionsDetail: Option[String]) extends WithId[OrderItemId]

case class DetailedOrderItem(orderItem: OrderItem, product: Product)

class OrderItems(tag: SlickTag) extends IdTable[OrderItemId, OrderItem](tag, "order_items"){
  override def * = (id.?, order, item, count, cost, price, retailPrice, oldCost, oldPrice, oldRetailPrice, fractionsDetail) <>(OrderItem.tupled, OrderItem.unapply)

  def order = column[OrderId]("order_id")
  def item = column[ItemId]("item")
  def count = column[Double]("count")
  def cost = column[Double]("cost")
  def price = column[Double]("price")
  def retailPrice = column[Double]("retail_price")
  def oldCost = column[Double]("old_cost")
  def oldPrice = column[Double]("old_price")
  def oldRetailPrice = column[Double]("old_retail_price")
  def fractionsDetail = column[Option[String]]("fractions_detail")

  def itemIdIdx = index("idx_order_item", item, unique = false)
  def orderFk = foreignKey("fk_order_item", order, ordersQuery)(_.id)
}