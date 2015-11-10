package repositories

import models.db.{Item, ItemId, Items}
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.dbio.DBIO

object ItemsRepository extends BaseIdRepository[ItemId, Item, Items](itemsQuery) {
  def findByName(name: String): DBIO[Option[Item]] = {
    val tag = query.filter(_.name === name)
    tag.result.headOption
  }
}