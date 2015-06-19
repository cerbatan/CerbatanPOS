package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models.Role.Seller
import play.api.mvc.Controller


object Application extends Controller with AuthElement with AuthConfiguration {
  def main = StackAction(AuthorityKey -> Seller) { implicit request =>
    Ok(views.html.main("Cerbatan POS"))
  }

  def sell = StackAction(AuthorityKey -> Seller) { implicit request =>
    Ok(views.html.sell())
  }

  def navigation = StackAction(AuthorityKey -> Seller) { implicit request =>
    Ok(views.html.navigation())
  }

  def header = StackAction(AuthorityKey -> Seller) { implicit request =>
    val user = loggedIn
    Ok(views.html.header(user))
  }

}