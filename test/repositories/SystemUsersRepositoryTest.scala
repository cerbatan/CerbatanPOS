package repositories

import models.db.SystemUser
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.db.slick.DB
import play.api.test.{PlaySpecification, WithApplication}

class SystemUsersRepositoryTest extends PlaySpecification {

  "System Users Service" should {

    "save and query users" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val usersRepository = SystemUserRepository

        val user = SystemUser(None, "userId", "theRole")
        val userId = usersRepository save user
        val userOpt = usersRepository findById userId

        userOpt.map(_.userId) shouldEqual Some(user.userId)
        userOpt.map(_.role) shouldEqual Some(user.role)
      }
    }

    "correctly find by userId" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val usersRepository = SystemUserRepository

        val user = SystemUser(None, "theUserId", "theRole")
        val userId = usersRepository save user
        val userOpt = usersRepository findByUserId  "theUserId"

        userOpt.map(_.userId) shouldEqual Some(user.userId)
        userOpt.map(_.role) shouldEqual Some(user.role)
      }
    }
  }

}