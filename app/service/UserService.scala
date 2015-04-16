package service

import models.db._
import org.joda.time.DateTime
import repositories.{AuthenticationProfileRepository, SystemUserRepository, TokenRepository}
import securesocial.core.providers.MailToken
import securesocial.core.services.SaveMode
import securesocial.core.{BasicProfile, PasswordInfo}

import scala.concurrent.Future
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


class UserService()(implicit application: play.api.Application) extends securesocial.core.services.UserService[LoginUser] {
  override def find(providerId: String, userId: String): Future[Option[BasicProfile]] =
    DB.withSession { implicit session: Session =>
      val profileRepository = AuthenticationProfileRepository
      val authProflie: Option[AuthenticationProfile] = profileRepository.findProfileByProviderAndUserId(providerId, userId)

      val profile = authProflie.map(x => Some(BasicProfileFromAuthenticationProfile(x))).getOrElse(None)

      Future.successful(profile)
    }

  override def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] =
    DB.withSession { implicit session: Session =>
      val profileRepository = AuthenticationProfileRepository
      val authProfile: Option[AuthenticationProfile] = profileRepository.findByEmailAndProvider(email, providerId)
      val profile = authProfile.map(x => Some(BasicProfileFromAuthenticationProfile(x))).getOrElse(None)

      Future.successful(profile)
    }

  override def deleteToken(uuid: String): Future[Option[MailToken]] = DB.withSession { implicit session: Session =>
    val tokenRepository = TokenRepository

    val token = tokenRepository.findById(uuid)

    if (!token.isEmpty) {
      tokenRepository.delete(uuid)
    }

    Future.successful(token)
  }

  def save(profile: BasicProfile, mode: SaveMode): Future[LoginUser] = {
    mode match {
      case SaveMode.SignUp => {
        saveNewProfile(profile)
      }

      case SaveMode.LoggedIn => {
        findLoginUserByProviderIdAndUserId(profile.providerId, profile.userId) match {
          case None => {
            saveNewProfile(profile)
          }
          case Some(existingUser) => {
            updateProfile(profile, existingUser)
          }
        }
      }

      case SaveMode.PasswordChange =>
        findLoginUserByProviderIdAndUserId(profile.providerId, profile.userId).map { u =>
          updateProfile(profile, u)
        }.getOrElse(Future.failed(new Exception("Missing Profile")))
    }
  }

  private def findLoginUserByProviderIdAndUserId(providerId: String, userId: String): Option[LoginUser] =
    DB.withSession { implicit session: Session =>
      val systemUserRepository = SystemUserRepository
      val systemUser = systemUserRepository.findByUserId(userId)

      systemUser match {
        case None =>
          None
        case Some(user) => {
          val profileRepository = AuthenticationProfileRepository
          val profiles = profileRepository.findByUserId(userId).map(BasicProfileFromAuthenticationProfile(_))

          profiles.find { p => p.providerId == providerId } match {
            case None =>
              None
            case Some(mainProfile) =>
              Some(LoginUser(user, mainProfile, profiles))
          }
        }
      }
    }

  def updateProfile(profile: BasicProfile, user: LoginUser): Future[LoginUser] =
    DB.withSession { implicit session: Session =>
      val profilesRepository = AuthenticationProfileRepository

      profilesRepository.updateWholeProfile(AuthenticationProfileFromBasicProfile(profile)) match {
        case Left(throwable) =>
          Future.failed(throwable)

        case Right(_) => {
          val profiles: List[BasicProfile] = user.profiles
          val updatedList: List[BasicProfile] = profiles.patch(profiles.indexWhere(_.providerId == profile.providerId), Seq(profile), 1)
          val loginUser: LoginUser = user.copy(profiles = updatedList)

          Future.successful(loginUser)
        }
      }
    }


  private def saveNewProfile(profile: BasicProfile): Future[LoginUser] = DB.withSession { implicit session: Session =>

    val userRepository = SystemUserRepository
    val user: SystemUser = new SystemUser(None, profile.userId)

    val savedUserId = userRepository.save(user)

    userRepository.findById(savedUserId) match {
      case None =>
        Future.failed(new scala.Exception("Failed to save new user"))
      case Some(x: SystemUser) =>
        val profileRepository = AuthenticationProfileRepository
        val savedId: ProfileId = profileRepository.save(AuthenticationProfileFromBasicProfile(profile))
        if (savedId.id > 0) {
          val loginUser = LoginUser(x, profile, List(profile))
          Future.successful(loginUser)
        } else {
          Future.failed(new scala.Exception("Failed to save new profile"))
        }
    }
  }

  override def link(current: LoginUser, to: BasicProfile): Future[LoginUser] =
    DB.withSession { implicit session: Session =>
      if (current.profiles.exists(p => p.providerId == to.providerId)) {
        Future.successful(current)

      } else {
        val profileRepository = AuthenticationProfileRepository
        val savedId: ProfileId = profileRepository.save(AuthenticationProfileFromBasicProfile(to))
        if (savedId.id > 0) {
          val updatedProfiles = to :: current.profiles
          val updatedUser = current.copy(profiles = updatedProfiles)

          Future.successful(updatedUser)
        } else {
          Future.failed(new scala.Exception("Failed to save new profile"))
        }
      }
    }

  override def passwordInfoFor(user: LoginUser): Future[Option[PasswordInfo]] = {
    Future.successful(user.main.passwordInfo)
  }

  override def findToken(token: String): Future[Option[MailToken]] = DB.withSession { implicit session: Session =>
    val tokenRepository = TokenRepository

    Future.successful(tokenRepository.findById(token))
  }

  override def updatePasswordInfo(user: LoginUser, info: PasswordInfo): Future[Option[BasicProfile]] =
    DB.withSession { implicit session: Session =>
      val profileRepository = AuthenticationProfileRepository
      val authProflie = profileRepository.findProfileByProviderAndUserId(user.main.providerId, user.main.userId)

      authProflie.map { p =>
        profileRepository.updatePasswordInfo(p, info) match {
          case Left(trowable) => Future.failed(trowable)
          case Right(profile) => Future.successful(Some(BasicProfileFromAuthenticationProfile(profile)))
        }
      }.getOrElse(Future.failed(new Exception("Could not find user profile")))
    }

  override def deleteExpiredTokens(): Unit = DB.withSession { implicit session: Session =>
    val tokenRepository = TokenRepository

    tokenRepository.deleteExpiredTokens(DateTime.now())
  }

  override def saveToken(token: MailToken): Future[MailToken] = DB.withSession { implicit session: Session =>
    val tokenRepository = TokenRepository

    Future.successful(tokenRepository.save(token))
  }
}

case class LoginUser(user: SystemUser, main: BasicProfile, profiles: List[BasicProfile])