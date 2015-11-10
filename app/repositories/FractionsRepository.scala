package repositories

import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.dbio.DBIO

object FractionsRepository extends BaseIdRepository[FractionId, Fraction, Fractions](fractionsQuery) {
  def findByItemId(itemId: ItemId): DBIO[Seq[Fraction]] = {
    val fractions = query.filter(_.item === itemId)
    fractions.result
  }
}
