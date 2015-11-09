package models.db

import java.time.LocalDateTime

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.lifted
import models.util.DateTimeSupport._


case class SaleId(id: Long) extends AnyVal with BaseId

object SaleId extends IdCompanion[SaleId]

case class Sale(id: Option[SaleId], datetime: LocalDateTime, invoiceNumber: Long) extends WithId[SaleId]

class Sales(tag: lifted.Tag) extends IdTable[SaleId, Sale](tag, "sales") {
  override def * = (id.?, datetime, invoiceNumber) <> (Sale.tupled, Sale.unapply)
  def datetime = column[LocalDateTime]("datetime")
  def invoiceNumber = column[Long]("invoice_number")


  def datetimeIdx = index("idx_sale_datetime", datetime, unique = false)
}