package controllers

import javax.inject.Inject

import jp.t2v.lab.play2.auth.LoginLogout
import models.Role.Owner
import models.db.SystemUser
import org.mindrot.jbcrypt.BCrypt
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.mvc.{Action, Controller}
import repositories.SystemUserRepository



import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class UserCredentials(email: String, password: String)

class MainController @Inject()(val dbConfigProvider: DatabaseConfigProvider, val systemUsers: SystemUserRepository) extends Controller with LoginLogout with AuthConfiguration {


  val loginForm = Form[UserCredentials] {
        mapping("email" -> email, "password" -> text)(UserCredentials.apply)(uc => Some((uc.email, "")))
  }

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded.map(_.flashing(
      "success" -> "You've been logged out"
    ))
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors))),
      userCredentials => {
        authenticateCredentials(userCredentials.email, userCredentials.password).flatMap(_ match {
          case None =>
            val invalidForm = loginForm.withGlobalError("Invalid email or password")
            Future.successful(BadRequest(views.html.login(invalidForm)))
          case Some(user) =>
            gotoLoginSucceeded(user.id.get.value)
        })
      }
    )
  }

}