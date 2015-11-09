package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.lifted.{Tag => SlickTag}

case class BrandId(id: Long) extends AnyVal with BaseId

object BrandId extends IdCompanion[BrandId]

case class Brand(id: Option[BrandId], name: String) extends WithId[BrandId]

class Brands(tag: SlickTag) extends IdTable[BrandId, Brand](tag, "brands") {
  override def * = (id.?, name) <>(Brand.tupled, Brand.unapply)

  def name = column[String]("name")

  def nameIdx = index("idx_brand_name", name, unique = true)
}