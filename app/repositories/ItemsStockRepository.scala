package repositories

import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.dbio.DBIO


object ItemsStockRepository extends BaseIdRepository[ItemStockId, ItemStock, ItemsStock](itemsStockQuery) {
  def findByItemId(itemId: ItemId): DBIO[Option[ItemStock]] = {
    val itemStock = query.filter(_.item === itemId)
    itemStock.result.headOption
  }
}
