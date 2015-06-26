package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import scala.slick.lifted

case class TagId(id: Long) extends AnyVal with BaseId

object TagId extends IdCompanion[TagId]

case class Tag(id: Option[TagId], name: String) extends WithId[TagId]

class Tags(tag: lifted.Tag) extends IdTable[TagId, Tag](tag, "tags") {
  override def * = (id.?, name) <> (Tag.tupled, Tag.unapply)
  def name = column[String]("name")
  def nameIdx = index("idx_tag_name", name, unique = true)
}
