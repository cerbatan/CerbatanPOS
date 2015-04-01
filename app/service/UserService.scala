package service

import models.{Tokens, BasicProfileFromUser, User, Users}
import play.api.Application
import securesocial.core.providers.MailToken
import securesocial.core.services.{SaveMode}
import securesocial.core.{PasswordInfo, BasicProfile}

import scala.concurrent.Future

import play.api.db.slick.Config.driver.simple._
import repositories.{TokenRepository, UserRepository}

class UserService()(implicit session: Session) extends securesocial.core.services.UserService[User] {
  override def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    val usersQuery: TableQuery[Users] = TableQuery[Users]
    val userRepository: UserRepository = new UserRepository(usersQuery)
    val user: Option[User] = userRepository.findUserByProvider(providerId, userId)
    val profile = user.map(x => Some(BasicProfileFromUser(x))).getOrElse(None)

    Future.successful(profile)
  }

  override def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {
    val usersQuery: TableQuery[Users] = TableQuery[Users]
    val userRepository: UserRepository = new UserRepository(usersQuery)
    val user: Option[User] = userRepository.findByEmailAndProvider(email, providerId)
    val profile = user.map(x => Some(BasicProfileFromUser(x))).getOrElse(None)

    Future.successful(profile)
  }

  override def deleteToken(uuid: String): Future[Option[MailToken]] = {
    val tokensQuery: TableQuery[Tokens] = TableQuery[Tokens]
    val tokenRepository = new TokenRepository(tokensQuery)

    val token = tokenRepository.findById(uuid)

    if ( !token.isEmpty ){
      tokenRepository.delete(uuid)
    }

    Future.successful(token)
  }

  override def link(current: User, to: BasicProfile): Future[Users] = ???

  override def passwordInfoFor(user: User): Future[Option[PasswordInfo]] = ???

  override def save(profile: BasicProfile, mode: SaveMode): Future[Users] = ???

  override def findToken(token: String): Future[Option[MailToken]] = ???

  override def deleteExpiredTokens(): Unit = ???

  override def updatePasswordInfo(user: User, info: PasswordInfo): Future[Option[BasicProfile]] = ???

  override def saveToken(token: MailToken): Future[MailToken] = ???
}

case class DemoUser(main: BasicProfile, identities: List[BasicProfile])