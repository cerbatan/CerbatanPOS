package models.db

import java.time.LocalDateTime

import models.util.DateTimeSupport._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.lifted.{Tag => SlickTag}


case class SaleId(id: Long) extends AnyVal with BaseId

object SaleId extends IdCompanion[SaleId]

case class Sale(id: Option[SaleId], datetime: LocalDateTime, invoiceNumber: Option[Long]) extends WithId[SaleId]

case class DetailedSale(sale: Sale, items: Seq[SoldItem])

class Sales(tag: SlickTag) extends IdTable[SaleId, Sale](tag, "sales") {
  override def * = (id.?, datetime, invoiceNumber) <>(Sale.tupled, Sale.unapply)

  def datetime = column[LocalDateTime]("datetime")
  def invoiceNumber = column[Option[Long]]("invoice_number")

  def datetimeIdx = index("idx_sale_datetime", datetime, unique = false)
  def invoiceNumberIdx = index("idx_sale_invoice_number", invoiceNumber, unique = false)
}