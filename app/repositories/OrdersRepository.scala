package repositories

import javax.inject.{Inject, Singleton}

import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import slick.dbio.{DBIO => _}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class OrdersRepository @Inject()(val productsRepository: ProductsRepository){
  val query = OrdersItemsRepository.query

  def getDetailedItemsForOrder(orderId: OrderId): DBIO[Seq[OrderItem]] = {
    OrdersItemsRepository.getItemsForOrder(orderId).map(items => items.map(i => ) )
  }
}

object OrdersRepository extends BaseIdRepository[OrderId, Order, Orders](ordersQuery) {
  def getDetailedOrder(orderId: OrderId): DBIO[Option[DetailedOrderItem]] = {
    findById(orderId).flatMap(maybeOrder => maybeOrder match {
      case None =>
        DBIO.successful(None)
      case Some(order) =>
        OrdersItemsRepository.getItemsForOrder(orderId).map(items => Some(DetailedOrder(order, items)))
    })
  }
}

object OrdersItemsRepository extends BaseIdRepository[OrderItemId, OrderItem, OrderItems](orderItemsQuery) {
  def getItemsForOrder(orderId: OrderId): DBIO[Seq[OrderItem]] = {
    val items = for {
      i <- query if (i.order === orderId)
    } yield i

    items.result
  }

}