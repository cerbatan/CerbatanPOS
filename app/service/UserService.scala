package service

import models._
import play.api.db.slick.Config.driver.simple._
import repositories.{TokenRepository, UserRepository}
import securesocial.core.providers.MailToken
import securesocial.core.services.SaveMode
import securesocial.core.{BasicProfile, PasswordInfo}

import scala.concurrent.Future


class UserService()(implicit session: Session) extends securesocial.core.services.UserService[User] {
  override def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    val user: Option[User] = findeUserByProviderIdAndUserId(providerId, userId)
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

    if (!token.isEmpty) {
      tokenRepository.delete(uuid)
    }

    Future.successful(token)
  }

  def save(profile: BasicProfile, mode: SaveMode): Future[User] = {
    mode match {
      case SaveMode.SignUp => {
        saveNewProfile(profile)
      }

      case SaveMode.LoggedIn => {
        findeUserByProviderIdAndUserId(profile.providerId, profile.userId) match {
          case None => {
            saveNewProfile(profile)
          }
          case Some(existingUser) => {
            updateProfile(profile, existingUser)
          }
        }
      }

      case SaveMode.PasswordChange =>
    }

  }

  private def findeUserByProviderIdAndUserId(providerId: String, userId: String): Option[User] = {
    val usersQuery: TableQuery[Users] = TableQuery[Users]
    val userRepository: UserRepository = new UserRepository(usersQuery)
    val user: Option[User] = userRepository.findUserByProvider(providerId, userId)
    user
  }

  def updateProfile(profile: BasicProfile, user: User): Future[User] = {
    val usersQuery: TableQuery[Users] = TableQuery[Users]
    val userRepository: UserRepository = new UserRepository(usersQuery)

    val newUserWithProfile = UserFromBasicProfile(profile)
    val updatedUser = newUserWithProfile.copy(id = user.id, userId = user.userId, providerId = user.providerId)


    userRepository.updateWholeUserProfile(updatedUser).fold(
      error => Future.failed(error),
      user => Future.successful(user)
    )
  }

  private def saveNewProfile(profile: BasicProfile): Future[User] = {
    val usersQuery: TableQuery[Users] = TableQuery[Users]
    val userRepository: UserRepository = new UserRepository(usersQuery)

    val savedId: UserId = userRepository.save(UserFromBasicProfile(profile))
    userRepository.findById(savedId).map(
      Future.successful(_)
    ).getOrElse(Future.failed(new scala.Exception("Failed to save new profile)")))
  }

  def link(current: User, to: BasicProfile): Future[User] = {

  }

  def passwordInfoFor(user: User): Future[Option[PasswordInfo]] = ???

  def updatePasswordInfo(user: User, info: PasswordInfo): Future[Option[BasicProfile]] = ???

  def deleteExpiredTokens(): Unit = ???

  def findToken(token: String): Future[Option[MailToken]] = ???

  def saveToken(token: MailToken): Future[MailToken] = ???
}

case class LoginUser(main: BasicProfile, identities: List[BasicProfile])