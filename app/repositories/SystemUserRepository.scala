package repositories

import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._



object SystemUserRepository extends BaseIdRepository[UserId, SystemUser, SystemUsers](systemUsersQuery) {
  def findByUserId(userId: String)(implicit session: Session): Option[SystemUser] = {
    val user = query.filter(_.userId === userId)
    user.firstOption
  }
}
