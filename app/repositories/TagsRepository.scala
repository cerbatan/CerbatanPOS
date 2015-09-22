package repositories

import models.db.{ItemId, Tag, TagId, Tags}
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

object TagsRepository extends BaseIdRepository[TagId, Tag, Tags](tagsQuery) {
  def findByName(name: String)(implicit session: Session): Option[Tag] = {
    val tag = query.filter(_.name === name)
    tag.firstOption
  }

  def filter(pattern: String)(implicit session: Session): List[Tag] = {
    val tags = for {tag <- query if tag.name.toLowerCase like s"%${pattern.toLowerCase()}%"} yield tag
    tags.list
  }

  def getTagsForItem(id: ItemId)(implicit session: Session): List[Tag] = {
    (for {
      ti <- tagsForItemQuery
      t <- query if ti.tag === t.id && ti.item === id
    } yield t).list
  }
}