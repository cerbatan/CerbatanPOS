package controllers.sell

import javax.inject.Inject

import controllers.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import models.Role.Seller
import play.api.db.slick._
import play.api.libs.json.Json
import play.api.mvc.Controller
import repositories.{ProductsRepository, SystemUserRepository}

import common.format.products._

import scala.concurrent.ExecutionContext.Implicits.global

class PointOfSale @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                            val systemUsers: SystemUserRepository,
                            val productsRepository: ProductsRepository) extends Controller with AuthElement with AuthConfiguration {

  def getListedProducts(query: String) = AsyncStack(AuthorityKey -> Seller) { implicit request =>
    val productsDBAction = productsRepository.getListedProducts(query)
    db.run(productsDBAction).map(products => Ok(Json.toJson(products)))
  }
}
