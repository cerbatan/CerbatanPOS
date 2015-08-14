package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import repositories._
import scala.slick.lifted

case class FractionId(id: Long) extends AnyVal with BaseId

object FractionId extends IdCompanion[FractionId]

case class Fraction(id: Option[FractionId], item: Option[ItemId], sku: String, name: String, retailPrice: Double, proportion: Float) extends WithId[FractionId]

class Fractions(tag: lifted.Tag) extends IdTable[FractionId, Fraction](tag, "fractions") {
  override def * = (id.?, item.?, sku, name, retailPrice, proportion) <> (Fraction.tupled, Fraction.unapply)
  def item = column[ItemId]("item")
  def sku = column[String]("sku")
  def name = column[String]("name")
  def retailPrice = column[Double]("price")
  def proportion = column[Float]("proportion")

  def skuIdx = index("idx_fraction_sku", name, unique = true)
  def itemFk = foreignKey("fk_item_fraction", item, itemsQuery)(_.id)
}