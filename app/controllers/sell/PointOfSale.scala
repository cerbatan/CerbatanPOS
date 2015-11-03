package controllers.sell

import controllers.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import models.ListedProduct
import models.Role.Seller
import play.api.db.slick._
import play.api.libs.json.Json
import play.api.mvc.Controller
import repositories.ProductsRepository
import common.format.products._
import play.api.Play.current

object PointOfSale extends Controller with AuthElement with AuthConfiguration {
  def getListedProducts(query: String) = StackAction(AuthorityKey -> Seller) { implicit request =>
    DB.withSession { implicit session: Session =>
      val products = ProductsRepository.getListedProducts(query)
      Ok(Json.toJson(products))
    }
  }
}
