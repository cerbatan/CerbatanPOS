package repositories

import models.Tokens
import org.joda.time.DateTime
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test.{PlaySpecification, WithApplication}
import securesocial.core.providers.MailToken

class TokenRepositoryTest extends PlaySpecification {

  "Tokens Service" should {

    "save and query tokens by id" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val tokensQuery = TableQuery[Tokens]
        val tokenRepository = new TokenRepository(tokensQuery)

        val token = MailToken("uuid", "the@mail.com", DateTime.parse("2015-10-03T16:00:45.123"), DateTime.parse("2015-10-03T16:00:45.123"), false)
        val savedToken = tokenRepository save token
        val queriedToken = tokenRepository findById savedToken.uuid

        queriedToken.map(_.uuid) shouldEqual Some(token.uuid)
        queriedToken.map(_.email) shouldEqual Some(token.email)
        queriedToken.map(_.creationTime) shouldEqual Some(token.creationTime)
        queriedToken.map(_.expirationTime) shouldEqual Some(token.expirationTime)
        queriedToken.map(_.isSignUp) shouldEqual Some(token.isSignUp)
      }
    }

    "correctly delete tokens" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val tokensQuery = TableQuery[Tokens]
        val tokenRepository = new TokenRepository(tokensQuery)

        val token = MailToken("uuid", "the@mail.com", DateTime.parse("2015-10-03T16:00:45.123"), DateTime.parse("2015-10-03T16:00:45.123"), false)
        val savedToken = tokenRepository save token

        tokenRepository delete token.uuid

        val queriedToken = tokenRepository findById savedToken.uuid

        queriedToken should beEmpty
      }
    }

    "correctly delete expired token" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val tokensQuery = TableQuery[Tokens]
        val tokenRepository = new TokenRepository(tokensQuery)

        val token = MailToken("uuid", "the@mail.com", DateTime.parse("2015-10-03T16:00:45.123"), DateTime.parse("2015-10-03T16:00:45.123"), false)
        val savedToken = tokenRepository save token

        tokenRepository deleteExpiredTokens  DateTime.parse("2015-10-03T17:00:45.123")
        val queriedToken = tokenRepository findById savedToken.uuid

        queriedToken should beEmpty
      }
    }

    "correctly conserve non expired token" in new WithApplication {
      DB.withSession { implicit session: Session =>
        val tokensQuery = TableQuery[Tokens]
        val tokenRepository = new TokenRepository(tokensQuery)

        val token = MailToken("uuid", "the@mail.com", DateTime.parse("2015-10-03T16:00:45.123"), DateTime.parse("2015-10-03T16:00:45.123"), false)
        val savedToken = tokenRepository save token

        tokenRepository deleteExpiredTokens  DateTime.parse("2015-10-03T12:00:45.123")
        val queriedToken = tokenRepository findById savedToken.uuid

        queriedToken should not beEmpty
      }
    }
  }

}