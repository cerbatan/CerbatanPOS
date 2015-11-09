package repositories

import models.Role.Guest
import models.db.SystemUser
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._

import play.api.test.{PlaySpecification, WithApplication}

class SystemUsersRepositoryTest extends PlaySpecification {

  "System Users Service" should {

    "save and query users" in new WithApplication {
//      DB.withSession { implicit session: Session =>
//        val usersRepository = SystemUserRepository
//
//        val user = SystemUser(None, Guest, "user@email", "password", Some("fullName"))
//        val userId = usersRepository save user
//        val userOpt = usersRepository findById userId
//
//        userOpt.map(_.email) shouldEqual Some(user.email)
//        userOpt.map(_.role) shouldEqual Some(user.role)
//      }
    }

    "correctly find by userId" in new WithApplication {
//      DB.withSession { implicit session: Session =>
//        val usersRepository = SystemUserRepository
//
//        val user = SystemUser(None, Guest, "user@email", "password", Some("fullName"))
//        val userId = usersRepository save user
//        val userOpt = usersRepository findByEmail   "user@email"
//
//        userOpt.map(_.email) shouldEqual Some(user.email)
//        userOpt.map(_.role) shouldEqual Some(user.role)
//      }
    }
  }

}