package repositories

import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

object ItemsRepository extends BaseIdRepository[ItemId, Item, Items](itemsQuery) {
  def findByName(name: String)(implicit session: Session): Option[Item] = {
    val tag = query.filter(_.name === name)
    tag.firstOption
  }
}