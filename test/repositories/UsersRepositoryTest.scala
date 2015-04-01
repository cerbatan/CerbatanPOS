package repositories

import models.{User, Users}
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.db.slick.DB
import play.api.test.{PlaySpecification, WithApplication}

import securesocial.core.AuthenticationMethod

class UsersRepositoryTest extends PlaySpecification {

  "Users Service" should {

    "save and query users" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val usersQuery: TableQuery[Users] = TableQuery[Users]
        val usersRepository = new UserRepository(usersQuery)

        val user = User(None, "UserPassword", "userId", Some("firstName"), Some("lastName"), Some("fullName"), Some("test@email.com"), None, AuthenticationMethod.UserPassword)
        val userId = usersRepository save user
        val userOpt = usersRepository findById userId

        userOpt.map(_.email) shouldEqual Some(user.email)
        userOpt.map(_.firstName) shouldEqual Some(user.firstName)
        userOpt.map(_.lastName) shouldEqual Some(user.lastName)
        userOpt.flatMap(_.id) should not be (None)
      }
    }

    "save and query by email and provider id" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val usersQuery: TableQuery[Users] = TableQuery[Users]
        val usersRepository = new UserRepository(usersQuery)

        val user = User(None, "UserPassword", "userId", Some("firstName"), Some("lastName"), Some("fullName"), Some("test@email.com"), None, AuthenticationMethod.UserPassword)
        val userId = usersRepository save user
        val userOpt = usersRepository findByEmailAndProvider ( user.email.get, user.providerId )

        userOpt.map(_.email) shouldEqual Some(user.email)
        userOpt.map(_.firstName) shouldEqual Some(user.firstName)
        userOpt.map(_.lastName) shouldEqual Some(user.lastName)
        userOpt.flatMap(_.id) should not be (None)
      }
    }

    "save and query by provider id" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val usersQuery: TableQuery[Users] = TableQuery[Users]
        val usersRepository = new UserRepository(usersQuery)

        val user = User(None, "UserPassword", "userId", Some("firstName"), Some("lastName"), Some("fullName"), Some("test@email.com"), None, AuthenticationMethod.UserPassword)
        val userId = usersRepository save user
        val userOpt = usersRepository findUserByProvider ( user.providerId, user.userId )

        userOpt.map(_.email) shouldEqual Some(user.email)
        userOpt.map(_.firstName) shouldEqual Some(user.firstName)
        userOpt.map(_.lastName) shouldEqual Some(user.lastName)
        userOpt.flatMap(_.id) should not be (None)
      }
    }

    "update user" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val usersQuery: TableQuery[Users] = TableQuery[Users]
        val usersRepository = new UserRepository(usersQuery)

        val user = User(None, "UserPassword", "userId", Some("firstName"), Some("lastName"), Some("fullName"), Some("test@email.com"), None, AuthenticationMethod.UserPassword)
        val userId = usersRepository save user
        val userOpt = usersRepository findUserByProvider ( user.providerId, user.userId )

        userOpt should not be None

        val userToUpdate = userOpt.get.copy(email = Some("new@email.com"), fullName = Some("newFullName"))

        val updateResult = usersRepository.updateWholeUserProfile(userToUpdate)

        updateResult must beRight

        val updateUser = usersRepository.findById(userOpt.get.id.get)


        updateUser.map(_.email) shouldEqual Some(userToUpdate.email)
        updateUser.map(_.fullName) shouldEqual Some(userToUpdate.fullName)
      }
    }
  }

}