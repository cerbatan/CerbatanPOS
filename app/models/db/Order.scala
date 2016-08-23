package models.db

import java.time.LocalDateTime
import models.util.DateTimeSupport._

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.lifted.{Tag => SlickTag}


case class OrderId(id: Long) extends AnyVal with BaseId
object OrderId extends IdCompanion[OrderId]

case class Order(id: Option[OrderId], status: Int, supplier: String, invoice: Option[String], createdDateTime: LocalDateTime, receivedDateTime: Option[LocalDateTime]) extends WithId[OrderId]
case class DetailedOrder(order: Order, items: Seq[DetailedOrderItem])

class Orders(tag: SlickTag) extends IdTable[OrderId, Order](tag, "orders") {
  override def * = (id.?, status, supplier, invoice, createdDateTime, receivedDateTime) <>(Order.tupled, Order.unapply)

  def status = column[Int]("status")
  def supplier = column[String]("supplier")
  def invoice = column[Option[String]]("invoice")
  def createdDateTime = column[LocalDateTime]("created_timestamp")
  def receivedDateTime = column[Option[LocalDateTime]]("received_timestamp")

  def createdDatetimeIdx = index("idx_order_created_timestamp", createdDateTime, unique = false)
  def receivedDatetimeIdx = index("idx_order_received_timestamp", receivedDateTime, unique = false)
  def supplierIdx = index("idx_order_supplier", supplier, unique = false)
}

object OrderStatus {
  val OPEN: Int = 0
  val PLACED: Int = 1
  val RECEIVED: Int = 2
}