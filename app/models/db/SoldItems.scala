package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import repositories.salesQuery
import slick.lifted.{Tag => SlickTag}


case class SoldItemId(id: Long) extends AnyVal with BaseId

object SoldItemId extends IdCompanion[SoldItemId]

case class SoldItem(id: Option[SoldItemId], sale: SaleId, itemId: ItemId, fractionId: Option[FractionId], count: Int, soldPrice: Double, taxed: Double, cost: Double) extends WithId[SoldItemId]

class SoldItems(tag: SlickTag) extends IdTable[SoldItemId, SoldItem](tag, "sold_items") {
  override def * = (id.?, sale, itemId, fractionId, count, soldPrice, taxed, cost) <>(SoldItem.tupled, SoldItem.unapply)

  def sale = column[SaleId]("sale")

  def itemId = column[ItemId]("item_id")

  def fractionId = column[Option[FractionId]]("fraction_id")

  def count = column[Int]("count")

  def soldPrice = column[Double]("sold_price")

  def taxed = column[Double]("taxed")

  def cost = column[Double]("cost")

  def itemIdIdx = index("idx_sold_item_id", itemId, unique = false)

  def saleFk = foreignKey("fk_sold_item_sale", sale, salesQuery)(_.id)
}