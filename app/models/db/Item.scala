package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import scala.slick.lifted

case class ItemId(id: Long) extends AnyVal with BaseId

object ItemId extends IdCompanion[ItemId]

case class Item(id: Option[ItemId], sku: String, name: String, brand: Option[BrandId]) extends WithId[ItemId]

class Items(tag: lifted.Tag) extends IdTable[ItemId, Item](tag, "items") {
  override def * = (id.?, sku, name, brand) <> (Item.tupled, Item.unapply)
  def sku = column[String]("sku")
  def name = column[String]("name")
  def brand = column[Option[BrandId]]("brand")
  def skuIdx = index("idx_item_sku", sku, unique = true)
  def nameIdx = index("idx_item_name", name, unique = false)
}
