package controllers

import jp.t2v.lab.play2.auth._
import models.Role
import models.Role._
import models.db.{SystemUser, SystemUserId}
import org.mindrot.jbcrypt.BCrypt
import play.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
import repositories.SystemUserRepository
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect._

trait AuthConfiguration extends AuthConfig with HasDatabaseConfigProvider[JdbcProfile] {
  val systemUsers: SystemUserRepository

  type Id = Long

  type User = SystemUser

  type Authority = Role
  override lazy val idContainer = AsyncIdContainer(new TransparentIdContainer[Id])
  val idTag: ClassTag[Id] = classTag[Id]
  val sessionTimeoutInSeconds = 3600

  def resolveUser(id: Id)(implicit ctx: ExecutionContext) = db.run(systemUsers.findById(SystemUserId(id)))


  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = throw new AssertionError("don't use")

  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit ctx: ExecutionContext) = {
    Logger.info(s"authorizationFailed. userId: ${user.id}, userName: ${user.fullName}, authority: $authority")
    Future.successful(Forbidden("no permission"))
  }

  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext) = Future.successful((user.role, authority) match {
    case (Owner, _) => true
    case (Administrator, Administrator) => true
    case (Administrator, Seller) => true
    case (Administrator, Guest) => true
    case (Seller, Seller) => true
    case (Seller, Guest) => true
    case (Guest, Guest) => true
    case _ => false
  })

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Application.main))

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.MainController.login))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.MainController.login))

  def authenticateCredentials(email: String, password: String): Future[Option[SystemUser]] = {
    val user = systemUsers.findByEmail(email)

    db.run(user).flatMap[Option[SystemUser]](maybeUser =>{
      maybeUser match {
        case None =>
          if ( email == "admin@cerbatan.com" ){
            val adminUser = SystemUser(None, Owner, "admin@cerbatan.com", BCrypt.hashpw(password, BCrypt.gensalt()), Some("Admin"))
            db.run(systemUsers.save(adminUser).flatMap(uid => systemUsers.findById(uid)))

          } else {
            Future.successful(None)
          }
        case Some(user) =>
          if ( BCrypt.checkpw(password, user.password) ) Future.successful(Some(user)) else Future.successful(None)
      }
    })
  }
}

