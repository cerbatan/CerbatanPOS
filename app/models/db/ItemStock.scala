package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import repositories.itemsQuery
import scala.slick.lifted
import scala.slick.lifted.TableQuery

case class ItemStockId(id: Long) extends AnyVal with BaseId

object ItemStockId extends IdCompanion[ItemStockId]


case class ItemStock(id: Option[ItemStockId], item: ItemId, cost: Double, price: Double, tax: Option[TaxId],
                     retailPrice: Double, trackStock: Boolean, stockCount: Float, alertLowStock: Boolean, alertStockLevel: Float) extends WithId[ItemStockId]

class ItemsStock(tag: lifted.Tag) extends IdTable[ItemStockId, ItemStock](tag, "items_stock") {
  override def * = (id.?, item, cost, price, tax.?, retailPrice, trackStock, stockCount, alertLowStock, alertStockLevel) <> (ItemStock.tupled, ItemStock.unapply)
  def item = column[ItemId]("item")
  def cost = column[Double]("cost")
  def price = column[Double]("price")
  def tax = column[TaxId]("tax", O.Nullable)
  def retailPrice = column[Double]("retail_price")
  def trackStock = column[Boolean]("track_stock")
  def stockCount = column[Float]("stock_count")
  def alertLowStock = column[Boolean]("alert_stock")
  def alertStockLevel = column[Float]("alert_level")


  def itemFk = foreignKey("fk_item_stock", item, itemsQuery)(_.id)
}