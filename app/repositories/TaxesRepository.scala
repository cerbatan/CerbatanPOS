package repositories

import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.dbio.DBIO

object TaxesRepository extends BaseIdRepository[TaxId, Tax, Taxes](taxesQuery) {
  def findByName(name: String): DBIO[Option[Tax]] = {
    val tag = query.filter(_.name === name)
    tag.result.headOption
  }
}