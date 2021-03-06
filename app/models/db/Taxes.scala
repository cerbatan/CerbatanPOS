package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.lifted.{Tag => SlickTag}


case class TaxId(id: Long) extends AnyVal with BaseId

object TaxId extends IdCompanion[TaxId]

case class Tax(id: Option[TaxId], name: String, percentage: Float) extends WithId[TaxId]

class Taxes(tag: SlickTag) extends IdTable[TaxId, Tax](tag, "taxes") {
  override def * = (id.?, name, percentage) <>(Tax.tupled, Tax.unapply)

  def name = column[String]("name")

  def percentage = column[Float]("percentage")

  def nameIdx = index("idx_tax_name", name, unique = true)
}