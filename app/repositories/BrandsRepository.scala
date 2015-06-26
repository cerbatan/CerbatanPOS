package repositories

import models.db.{Brands, Brand, BrandId}
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

object BrandsRepository extends BaseIdRepository[BrandId, Brand, Brands](brandsQuery) {
  def findByName(name: String)(implicit session: Session): Option[Brand] = {
    val brand = query.filter(_.name === name)
    brand.firstOption
  }

  def filter(pattern: String)(implicit session: Session) : List[Brand] = {
    val brands = for { brand <- query if brand.name.toLowerCase like s"%${pattern.toLowerCase()}%"  } yield brand
    brands.list
  }
}
