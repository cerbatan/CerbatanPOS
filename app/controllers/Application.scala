package controllers

import jp.t2v.lab.play2.auth.AuthElement
import models.Role.{Guest, Seller}
import play.api.Routes
import play.api.mvc.{Action, Controller}

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

  def jsRoutes = StackAction(AuthorityKey -> Guest) { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        controllers.products.routes.javascript.Products.brands,
        controllers.products.routes.javascript.Products.addBrand,
        controllers.products.routes.javascript.Products.tags,
        controllers.products.routes.javascript.Products.addTag,
        controllers.products.routes.javascript.Products.taxes,
        controllers.products.routes.javascript.Products.addTax,
        controllers.products.routes.javascript.Products.saveProduct,
        controllers.products.routes.javascript.Products.updateProduct,
        controllers.products.routes.javascript.Products.getProduct,
        controllers.products.routes.javascript.Products.getProductsBrief,
        controllers.sell.routes.javascript.PointOfSale.getListedProducts
      )
    ).as(JAVASCRIPT)
  }

}