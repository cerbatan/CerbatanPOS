package repositories

import models.{UserId, User, Users}
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._


class UserRepository(query: TableQuery[Users])
  extends BaseIdRepository[UserId, User, Users](query) {
  def findByEmailAndProvider(email: String, providerId: String)(implicit session: Session): Option[User] = {
    val user = for(
      u <- query
      if u.providerId === providerId && u.email === email
    ) yield  u

    user.firstOption
  }

  def findUserByProvider(providerId: String, userId: String)(implicit session: Session): Option[User] = {
    val user = for(
      u <- query
      if u.providerId === providerId && u.userId === userId
    ) yield  u

    user.firstOption
  }
}

