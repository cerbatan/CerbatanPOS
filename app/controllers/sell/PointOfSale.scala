package controllers.sell

import javax.inject.Inject

import common.format.products._
import controllers.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import models.Role.Seller
import models.SaleDetails
import play.api.db.slick._
import play.api.libs.json.Json
import play.api.mvc.{BodyParsers, Controller}
import processors.SalesProcessor
import repositories.{SalesRepository, ProductsRepository, SystemUserRepository}
import common.format.sale._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PointOfSale @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                            val systemUsers: SystemUserRepository,
                            val productsRepository: ProductsRepository,
                            val salesProcessor: SalesProcessor) extends Controller with AuthElement with AuthConfiguration {

  def getListedProducts(query: String) = AsyncStack(AuthorityKey -> Seller) { implicit request =>
    val productsDBAction = productsRepository.getListedProducts(query)
    db.run(productsDBAction).map(products => Ok(Json.toJson(products)))
  }

  def registerSale = AsyncStack(BodyParsers.parse.json, AuthorityKey -> Seller) { implicit request =>
    val jsResult = request.body.validate[SaleDetails]

    jsResult fold(
      error => {
        Future.successful(BadRequest)
      },
      saleDetails => {
        salesProcessor.receive(saleDetails)(dbConfig).map( r => Ok(Json.toJson(r)) )
      })

//    Future.successful(NotImplemented)
  }
}
