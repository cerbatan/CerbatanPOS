package repositories

import models.db.Tag
import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

object TaxesRepository extends BaseIdRepository[TaxId, Tax, Taxes](taxesQuery) {
  def findByName(name: String)(implicit session: Session): Option[Tax] = {
    val tag = query.filter(_.name === name)
    tag.firstOption
  }
}