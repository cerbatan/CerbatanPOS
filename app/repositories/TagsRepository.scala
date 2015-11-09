package repositories

import models.db.{ItemId, Tag, TagId, Tags}
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._

import slick.dbio.DBIO

object TagsRepository extends BaseIdRepository[TagId, Tag, Tags](tagsQuery) {
  def findByName(name: String): DBIO[Option[Tag]] = {
    val tag = query.filter(_.name === name)
    tag.result.headOption
  }

  def filter(pattern: String): DBIO[Seq[Tag]] = {
    val tags = for {tag <- query if tag.name.toLowerCase like s"%${pattern.toLowerCase()}%"} yield tag
    tags.result
  }

  def getTagsForItem(id: ItemId): DBIO[Seq[Tag]] = {
    (for {
      ti <- tagsForItemQuery
      t <- query if ti.tag === t.id && ti.item === id
    } yield t).result
  }
}