package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.lifted
import slick.lifted.ProvenShape

case class TagId(id: Long) extends AnyVal with BaseId

object TagId extends IdCompanion[TagId]

case class Tag(id: Option[TagId], name: String) extends WithId[TagId]

class Tags(tag: lifted.Tag) extends IdTable[TagId, Tag](tag, "tags") {
  override def * = (id.?, name) <> (Tag.tupled, Tag.unapply)
  def name = column[String]("name")
  def nameIdx = index("idx_tag_name", name, unique = true)
}

class TagsForItem(t: lifted.Tag) extends Table[(TagId, ItemId)](t, "tags_for_items"){
  def tag = column[TagId]("tag")
  def item = column[ItemId]("item")

  override def * = (tag, item)
  def idx = index("idx_tags_for_item", (tag, item), unique = true)
  def tagFk = foreignKey("tags_for_item_tag_fk", tag, repositories.tagsQuery)(_.id, onDelete = ForeignKeyAction.Cascade)
  def itemFk = foreignKey("tags_for_item_item_fk", item, repositories.itemsQuery)(_.id, onDelete = ForeignKeyAction.Cascade)
}