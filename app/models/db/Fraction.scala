package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import repositories._
import scala.slick.lifted

case class FractionId(id: Long) extends AnyVal with BaseId

object FractionId extends IdCompanion[FractionId]

case class Fraction(id: Option[FractionId], item: Option[ItemId], name: String, retailPrice: Double, quantityInPack: Int) extends WithId[FractionId]

class Fractions(tag: lifted.Tag) extends IdTable[FractionId, Fraction](tag, "fractions") {
  override def * = (id.?, item.?, name, retailPrice, quantityInPack) <> (Fraction.tupled, Fraction.unapply)
  def item = column[ItemId]("item")
  def name = column[String]("name")
  def retailPrice = column[Double]("price")
  def quantityInPack = column[Int]("qty_in_pack")

  def itemFk = foreignKey("fk_item_fraction", item, itemsQuery)(_.id)
}