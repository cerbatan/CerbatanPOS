package repositories

import models.db.AuthenticationProfile
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.db.slick.DB
import play.api.test.{PlaySpecification, WithApplication}
import securesocial.core.AuthenticationMethod

class AuthenticationProfileRepositoryTest extends PlaySpecification {

  "Authentication Profile Service" should {

    "save and query authentication profiles" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val profilesRepository = AuthenticationProfileRepository

        val profile = AuthenticationProfile(None, "UserPassword", "userId", Some("firstName"), Some("lastName"), Some("fullName"), Some("test@email.com"), None, AuthenticationMethod.UserPassword)
        val profileId = profilesRepository save profile
        val maybeProfile = profilesRepository findById profileId

        maybeProfile.map(_.email) shouldEqual Some(profile.email)
        maybeProfile.map(_.firstName) shouldEqual Some(profile.firstName)
        maybeProfile.map(_.lastName) shouldEqual Some(profile.lastName)
        maybeProfile.flatMap(_.id) should not be (None)
      }
    }

    "save and query authentication profiles by userId" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val profilesRepository = AuthenticationProfileRepository

        val profile = AuthenticationProfile(None, "UserPassword", "userId", Some("firstName"), Some("lastName"), Some("fullName"), Some("test@email.com"), None, AuthenticationMethod.UserPassword)
        val profile2 = AuthenticationProfile(None, "LinkedIn", "userId", Some("firstName"), Some("lastName"), Some("fullName"), Some("test@email.com"), None, AuthenticationMethod.UserPassword)
        profilesRepository save profile
        profilesRepository save profile2
        val profiles = profilesRepository findByUserId  "userId"

        profiles.length shouldEqual 2
      }
    }
  }

}

