package repositories

import models.db.{AuthenticationProfile, AuthenticationProfiles, ProfileId}
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import securesocial.core.PasswordInfo

object AuthenticationProfileRepository extends BaseIdRepository[ProfileId, AuthenticationProfile, AuthenticationProfiles](authenticationProfilesQuery) {
  def updatePasswordInfo(currentProfile: AuthenticationProfile, updatedPasswordInfo: PasswordInfo)
                        (implicit session: Session) : Either[Throwable, AuthenticationProfile] = {
    val q = for {p <- query
                 if p.userId === currentProfile.userId && p.providerId === currentProfile.providerId
    } yield (p.hasher, p.password, p.salt)

    if (q.update(Some(updatedPasswordInfo.hasher), Some(updatedPasswordInfo.password), updatedPasswordInfo.salt) > 0) {
      Right(currentProfile.copy(passwordInfo = Some(updatedPasswordInfo)))
    } else {
      Left(new Exception("Failed to update profile"))
    }
  }

  def updateWholeProfile(authProfile: AuthenticationProfile)(implicit session: Session): Either[Throwable, AuthenticationProfile] = {
    val q = for {p <- query if p.userId === authProfile.userId && p.providerId === authProfile.providerId} yield p

    if (q.update(authProfile) > 0) {
      Right(authProfile)
    } else {
      Left(new Exception("Failed to update profile"))
    }
  }

  def findByUserId(userId: String)(implicit session: Session): List[AuthenticationProfile] = {
    val profiles = for (
      p <- query
      if p.userId === userId
    ) yield p

    profiles.list
  }

  def findByEmailAndProvider(email: String, providerId: String)(implicit session: Session): Option[AuthenticationProfile] = {
    val profile = for (
      p <- query
      if p.providerId === providerId && p.email === email
    ) yield p

    profile.firstOption
  }

  def findProfileByProviderAndUserId(providerId: String, userId: String)(implicit session: Session): Option[AuthenticationProfile] = {
    val profile = for (
      p <- query
      if p.providerId === providerId && p.userId === userId
    ) yield p

    profile.firstOption
  }

}

