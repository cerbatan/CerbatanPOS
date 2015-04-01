package repositories

import models.{UserId, User, Users}
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

import scala.concurrent.Future
import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.ShapedValue


class UserRepository(query: TableQuery[Users])
  extends BaseIdRepository[UserId, User, Users](query) {
  def updateWholeUserProfile(user: User)(implicit session: Session): Either[Throwable, User] = {
    val q = for { u <- query if u.id === user.id } yield u

    if ( q.update(user) > 0 ){
      Right(user)
    } else {
      Left(new Exception("Failed to update user"))
    }
  }

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

