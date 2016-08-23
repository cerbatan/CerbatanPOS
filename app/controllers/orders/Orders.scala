package controllers.orders

import java.time.LocalDateTime
import javax.inject.Inject

import controllers.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import models.Role.{Administrator, Seller}
import models.db.{Order, OrderId, OrderStatus}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.libs.functional.syntax._
import play.api.mvc.{BodyParsers, Controller}
import repositories.{OrdersRepository, SystemUserRepository}
import common.format.orders._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class Orders @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                       val systemUsers: SystemUserRepository) extends Controller with AuthElement with AuthConfiguration {

  case class NewOrder(id: String, supplier: String)
  implicit val newOrderReads: Reads[NewOrder] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "supplier").read[String]
    )(NewOrder.apply _)


  def list = StackAction(AuthorityKey -> Seller) { implicit request =>
    Ok(views.html.orders.orders())
  }

  def editOrder = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    Ok(views.html.orders.editOrder())
  }

  def add() = AsyncStack(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val supplierResult = request.body.\("supplier").validate[String]

    supplierResult.fold(
      errors => {
        Future.successful(BadRequest)
      },
      supplier => {
        val order = Order(None, OrderStatus.OPEN, supplier, None, LocalDateTime.now(), None)
        val saveOrder = OrdersRepository.save(order)

        db.run(saveOrder).map(orderId => Ok(Json.toJson(orderId)))
      }
    )
  }

  def getOrder(id: Long) = AsyncStack(AuthorityKey -> Seller) { implicit request =>
    val orderId = OrderId(id)
    val getOrderAction = OrdersRepository.getDetailedOrder(orderId)

    db.run(getOrderAction).map(order => Ok(Json.toJson(order)))
  }
}
