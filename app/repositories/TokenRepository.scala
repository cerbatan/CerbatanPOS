package repositories

import com.github.tototoshi.slick.H2JodaSupport._
import models.Tokens
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import securesocial.core.providers.MailToken

object TokenRepository extends BasicRepository[Tokens](tokensQuery) {
  def findById(tokenId: String)(implicit session: Session): Option[MailToken] = {
    val q = for {
      token <- query
      if token.uuid === tokenId
    } yield token

    q.firstOption
  }

  def save(token: MailToken)(implicit session: Session): MailToken = {
    findById(token.uuid) match {
      case None => {
        query.insert(token)
        token
      }
      case Some(existingToken) => {
        val tokenRow = for {
          t <- query
          if t.uuid === existingToken.uuid
        } yield t

        tokenRow.update(token)
        token
      }
    }
  }

  def delete(uuid: String)(implicit session: Session) = {
    val q = for {
      t <- query
      if t.uuid === uuid
    } yield t

    q.delete
  }

  def deleteExpiredTokens(currentDate: DateTime)(implicit session: Session) {
    val q = for {
      t <- query
      if t.expirationTime < currentDate
    } yield t

    q.delete
  }
}
