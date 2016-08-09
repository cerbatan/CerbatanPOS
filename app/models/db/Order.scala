package models.db

import java.time.LocalDateTime
import models.util.DateTimeSupport._

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.lifted.{Tag => SlickTag}


case class OrderId(id: Long) extends AnyVal with BaseId
object OrderId extends IdCompanion[OrderId]

case class Order(id: Option[OrderId], status: Int, supplier: String, invoice: String, receivedDateTime: Option[LocalDateTime]) extends WithId[OrderId]

class Orders(tag: SlickTag) extends IdTable[OrderId, Order](tag, "orders") {
  override def * = (id.?, status, supplier, invoice, receivedDateTime) <>(Order.tupled, Order.unapply)

  def supplier = column[String]("supplier")
  def status = column[Int]("status")
  def invoice = column[String]("invoice")
  def receivedDateTime = column[Option[LocalDateTime]]("received_timestamp")

  def datetimeIdx = index("idx_order_received_timestamp", receivedDateTime, unique = false)
  def supplierIdx = index("idx_order_supplier", supplier, unique = false)
}
