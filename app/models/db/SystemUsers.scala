package models.db

import models.Role
import models.Role.Guest
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._


import scala.slick.lifted.ProvenShape
import scala.slick.lifted

case class SystemUserId(id: Long) extends AnyVal with BaseId

object SystemUserId extends IdCompanion[SystemUserId]

/** User entity.
  */
case class SystemUser(id: Option[SystemUserId],
                      role: Role = Guest,
                      email: String,
                      password: String,
                      fullName: Option[String]
                      ) extends WithId[SystemUserId]


class SystemUsers(tag: lifted.Tag) extends IdTable[SystemUserId, SystemUser](tag, "SYSTEM_USERS") {
  implicit def string2Role = MappedColumnType.base[Role, String](
    role => role.toString,
    string => Role.valueOf(string)
  )


  def * : ProvenShape[SystemUser] = {
    val shapedValue = (id.?, role, email, password, fullName).shaped

    shapedValue.<>({
      tuple => SystemUser(id = tuple._1, role = tuple._2, email = tuple._3, password = tuple._4, fullName = tuple._5)
    }, {
      (u: SystemUser) => Some {
        (u.id, u.role, u.email, u.password, u.fullName)
      }
    })
  }

  def role = column[Role]("role")

  def email = column[String]("email")

  def password = column[String]("password")

  def fullName = column[Option[String]]("full_name")

  def emailIdx = index("idx_userEmailId", email, unique = true)
}
