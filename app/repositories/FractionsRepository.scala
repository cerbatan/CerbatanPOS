package repositories

import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

object FractionsRepository extends BaseIdRepository[FractionId, Fraction, Fractions](fractionsQuery) {
  def findByItemId(itemId: ItemId)(implicit session: Session): List[Fraction] = {
    val fractions = query.filter(_.item === itemId)
    fractions.list
  }
}
