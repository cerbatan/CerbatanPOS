package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

import scala.slick.lifted.ProvenShape

case class UserId(id: Long) extends AnyVal with BaseId

object UserId extends IdCompanion[UserId]

/** User entity.
  */
case class SystemUser(id: Option[UserId],
                userId: String,
                role: String = "guest") extends WithId[UserId]


class SystemUsers(tag: Tag) extends IdTable[UserId, SystemUser](tag, "SYSTEM_USERS") {
  def * : ProvenShape[SystemUser] = {
    val shapedValue = (id.?, userId, role).shaped

    shapedValue.<>({
      tuple => SystemUser(id = tuple._1, userId = tuple._2, role = tuple._3)
    }, {
      (u: SystemUser) => Some {
        (u.id, u.userId, u.role)
      }
    })
  }

  def userId = column[String]("userId")

  def role = column[String]("role")

  def userIdIdx = index("idx_userId", userId, unique = true)
}
