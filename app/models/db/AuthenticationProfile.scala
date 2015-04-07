package models.db

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import securesocial.core._


import scala.slick.lifted.ProvenShape

case class ProfileId(id: Long) extends AnyVal with BaseId
object ProfileId extends IdCompanion[ProfileId]

case class AuthenticationProfile(id: Option[ProfileId],
                   providerId: String,
                   userId: String,
                   firstName: Option[String],
                   lastName: Option[String],
                   fullName: Option[String],
                   email: Option[String],
                   avatarUrl: Option[String],
                   authMethod: AuthenticationMethod,
                   oAuth1Info: Option[OAuth1Info] = None,
                   oAuth2Info: Option[OAuth2Info] = None,
                   passwordInfo: Option[PasswordInfo] = None) extends WithId[ProfileId] with GenericProfile


object BasicProfileFromAuthenticationProfile {
  def apply(p: AuthenticationProfile): BasicProfile = BasicProfile(p.providerId, p.userId, p.firstName, p.lastName, p.fullName, p.email, p.avatarUrl, p.authMethod, p.oAuth1Info, p.oAuth2Info, p.passwordInfo)
}

object AuthenticationProfileFromBasicProfile {
  def apply(p: BasicProfile): AuthenticationProfile = AuthenticationProfile(None, p.providerId, p.userId, p.firstName, p.lastName, p.fullName, p.email, p.avatarUrl, p.authMethod, p.oAuth1Info, p.oAuth2Info, p.passwordInfo)
}

class AuthenticationProfiles(tag: Tag) extends IdTable[ProfileId, AuthenticationProfile](tag, "AUTH_PROFILES") {
  implicit def string2AuthenticationMethod = MappedColumnType.base[AuthenticationMethod, String](
    authenticationMethod => authenticationMethod.method,
    string => AuthenticationMethod(string)
  )

  implicit def tuple2OAuth1Info(tuple: (Option[String], Option[String])): Option[OAuth1Info] = tuple match {
    case (Some(token), Some(secret)) => Some(OAuth1Info(token, secret))
    case _ => None
  }

  implicit def tuple2OAuth2Info(tuple: (Option[String], Option[String], Option[Int], Option[String])): Option[OAuth2Info] = tuple match {
    case (Some(accessToken), tokenType, expiresIn, refreshToken) => Some(OAuth2Info(accessToken, tokenType, expiresIn, refreshToken))
    case _ => None
  }

  implicit def tuple2PasswordInfo(tuple: (Option[String], Option[String], Option[String])): Option[PasswordInfo] = tuple match {
    case (Some(hasher), Some(password), salt) => Some(PasswordInfo(hasher, password, salt))
    case _ => None
  }

  def providerId = column[String]("providerId")

  def userId = column[String]("userId")

  def firstName = column[Option[String]]("firstName")

  def lastName = column[Option[String]]("lastName")

  def fullName = column[Option[String]]("fullName")

  def email = column[Option[String]]("email")

  def avatarUrl = column[Option[String]]("avatarUrl")

  def authMethod = column[AuthenticationMethod]("authMethod")

  // oAuth 1
  def token = column[Option[String]]("token")

  def secret = column[Option[String]]("secret")

  // oAuth 2
  def accessToken = column[Option[String]]("accessToken")

  def tokenType = column[Option[String]]("tokenType")

  def expiresIn = column[Option[Int]]("expiresIn")

  def refreshToken = column[Option[String]]("refreshToken")

  // passwordInfo 1
  def hasher = column[Option[String]]("hasher")

  def password = column[Option[String]]("password")

  def salt = column[Option[String]]("salt")

  def userIdIdx = index("auth_idx_userId", userId, unique=false)
  def providerIdIdx = index("auth_idx_providerId", providerId, unique = false)

  def * : ProvenShape[AuthenticationProfile] = {
    val shapedValue = (id.?,
      providerId,
      userId,
      firstName,
      lastName,
      fullName,
      email,
      avatarUrl,
      authMethod,
      token,
      secret,
      accessToken,
      tokenType,
      expiresIn,
      refreshToken,
      hasher,
      password,
      salt
      ).shaped

    shapedValue.<>({
      tuple =>
        AuthenticationProfile.apply(id = tuple._1,
          providerId = tuple._2,
          userId = tuple._3,
          firstName = tuple._4,
          lastName = tuple._5,
          fullName = tuple._6,
          email = tuple._7,
          avatarUrl = tuple._8,
          authMethod = tuple._9,
          oAuth1Info = (tuple._10, tuple._11),
          oAuth2Info = (tuple._12, tuple._13, tuple._14, tuple._15),
          passwordInfo = (tuple._16, tuple._17, tuple._18)
        )
    }, {
      (u: AuthenticationProfile) =>
        Some {
          (
            u.id,
            u.providerId,
            u.userId,
            u.firstName,
            u.lastName,
            u.fullName,
            u.email,
            u.avatarUrl,
            u.authMethod,
            u.oAuth1Info.map(_.token),
            u.oAuth1Info.map(_.secret),
            u.oAuth2Info.map(_.accessToken),
            u.oAuth2Info.flatMap(_.tokenType),
            u.oAuth2Info.flatMap(_.expiresIn),
            u.oAuth2Info.flatMap(_.refreshToken),
            u.passwordInfo.map(_.hasher),
            u.passwordInfo.map(_.password),
            u.passwordInfo.flatMap(_.salt)
            )
        }
    }
    )
  }
}


