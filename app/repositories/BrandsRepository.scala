package repositories

import models.db.{Brand, BrandId, Brands}
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.dbio.DBIO

object BrandsRepository extends BaseIdRepository[BrandId, Brand, Brands](brandsQuery) {
  def findByName(name: String): DBIO[Option[Brand]] = {
    val brand = query.filter(_.name === name)
    brand.result.headOption
  }

  def filter(pattern: String): DBIO[Seq[Brand]] = {
    val brands = for {brand <- query if brand.name.toLowerCase like s"%${pattern.toLowerCase()}%"} yield brand
    brands.result
  }
}
