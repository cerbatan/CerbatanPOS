package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import scala.slick.lifted

case class TaxId(id: Long) extends AnyVal with BaseId

object TaxId extends IdCompanion[TaxId]

case class Tax(id: Option[TaxId], name: String, percentage: Float) extends WithId[TaxId]

class Taxes(tag: lifted.Tag) extends IdTable[TaxId, Tax](tag, "taxes") {
  override def * = (id.?, name, percentage) <> (Tax.tupled, Tax.unapply)
  def name = column[String]("name")
  def percentage = column[Float]("name")
  def nameIdx = index("idx_tax_name", name, unique = true)
}