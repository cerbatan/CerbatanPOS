package controllers

import jp.t2v.lab.play2.auth.{TransparentIdContainer,  AsyncIdContainer, AuthConfig}
import models.Role
import models.Role._
import models.db.{SystemUserId, SystemUser}
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
import repositories.SystemUserRepository
import scala.concurrent.{Future, ExecutionContext}
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import scala.reflect._
import play.Logger
import play.api.Play.current


trait AuthConfiguration extends AuthConfig {
  type Id = Long

  type User = SystemUser

  type Authority = Role
  override lazy val idContainer = AsyncIdContainer(new TransparentIdContainer[Id])
  val idTag: ClassTag[Id] = classTag[Id]
  val sessionTimeoutInSeconds = 3600

  def resolveUser(id: Id)(implicit ctx: ExecutionContext) = Future.successful(
    DB.withSession { implicit session: Session =>
      SystemUserRepository.findById(SystemUserId(id))
    }
  )

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
}

