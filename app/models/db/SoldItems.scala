package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.lifted.{Tag => SlickTag}


case class SoldItemId(id: Long) extends AnyVal with BaseId

object SoldItemId extends IdCompanion[SoldItemId]

case class SoldItem(id: Option[SoldItemId], itemId: ItemId, fractionId: Option[FractionId]) extends WithId[SoldItemId]

class SoldItems(tag: SlickTag) extends IdTable[SoldItemId, SoldItem](tag, "sold_items") {
  override def * = (id.?, itemId, fractionId) <>(SoldItem.tupled, SoldItem.unapply)

  def itemId = column[ItemId]("item_id")

  def fractionId = column[Option[FractionId]]("fraction_id")
}