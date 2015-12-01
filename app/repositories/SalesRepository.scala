package repositories

import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._


object SalesRepository extends BaseIdRepository[SaleId, Sale, Sales](salesQuery) {

}

object SoldItemsRepository extends BaseIdRepository[SoldItemId, SoldItem, SoldItems](soldItemsQuery) {

}