package repositories

import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

object ItemsStockRepository extends BaseIdRepository[ItemStockId, ItemStock, ItemsStock](itemsStockQuery) {
  def findByItemId(itemId: ItemId)(implicit session: Session): Option[ItemStock] = {
    val itemStock = query.filter(_.item === itemId)
    itemStock.firstOption
  }
}
