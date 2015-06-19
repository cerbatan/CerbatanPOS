package controllers.products

import controllers.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import models.Role.{Administrator, Seller}
import play.api.mvc.Controller

object Products extends Controller with AuthElement with AuthConfiguration {
  def products = StackAction(AuthorityKey -> Seller) { implicit request =>
    val user = loggedIn
    Ok(views.html.products.products())
  }

  def newProduct = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    Ok(views.html.products.productForm("Add Product"))
  }
}
