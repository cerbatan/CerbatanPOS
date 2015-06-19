package repositories

import models.Role.Owner
import models.db._
import org.mindrot.jbcrypt.BCrypt
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._




object SystemUserRepository extends BaseIdRepository[SystemUserId, SystemUser, SystemUsers](systemUsersQuery) {
  def findByEmail(email: String)(implicit session: Session): Option[SystemUser] = {
    val user = query.filter(_.email === email)
    user.firstOption
  }

  def authenticate(email: String, password: String)(implicit session: Session): Option[SystemUser] = {
      val user = findByEmail(email)

      if ( user.isEmpty && email == "admin@cerbatan.com" ){
        val adminUser = SystemUser(None, Owner, "admin@cerbatan.com", BCrypt.hashpw(password, BCrypt.gensalt()), Some("Admin"))
        val userId = save(adminUser)

        return findById(userId)
      } else {
        user.filter { systemUser => BCrypt.checkpw(password, systemUser.password) }
      }
  }
}
