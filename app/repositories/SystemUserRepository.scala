package repositories

import javax.inject.Singleton

import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.dbio.DBIO


@Singleton
class SystemUserRepository extends BaseIdRepository[SystemUserId, SystemUser, SystemUsers](systemUsersQuery)  {
  def findByEmail(email: String): DBIO[Option[SystemUser]] = {
    val user = query.filter(_.email === email)
    user.result.headOption
  }
}
