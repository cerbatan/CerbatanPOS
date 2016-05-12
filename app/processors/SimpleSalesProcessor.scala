package processors

import java.time.LocalDateTime

import models.SaleDetails
import models.db.{ItemsStock, DetailedSale, Sale, SoldItem}
import org.virtuslab.unicorn.LongUnicornPlay
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._
import processors.ResponseStatus.{ResponseStatus, Success}
import repositories._
import slick.backend.DatabaseConfig
import slick.dbio.{DBIO, DBIOAction}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

case class SimpleSalesProcessorResponse(status: ResponseStatus, errorString: Option[String], invoiceId: Option[Long]) extends SalesProcessorResponse

class SimpleSalesProcessor extends SalesProcessor {
  override def receive(saleDetails: SaleDetails)(implicit dbConfig: DatabaseConfig[JdbcProfile]) = {
    val (status, errorDescription) = validateItems(saleDetails.items)

    status match {
      case Success => {
        persistSale(saleDetails, dbConfig).map(detailedSale => SimpleSalesProcessorResponse(status, errorDescription, detailedSale.sale.invoiceNumber))
      }
      case error => Future.successful(SimpleSalesProcessorResponse(status, errorDescription, None))
    }
  }

  //Validate items may be used to check stock before saving
  def validateItems(items: Seq[SoldItem]): (ResponseStatus, Option[String]) = {
    (Success, None)
  }

  def persistSale(saleDetails: SaleDetails, dbConfig: DatabaseConfig[JdbcProfile]): Future[DetailedSale] = {
    val newSale = Sale(None, LocalDateTime.now, None)

    val saveSalesAction = SalesRepository.save(newSale).flatMap { saleId =>
      SalesRepository.findById(saleId).flatMap { savedSale =>
        val newSoldItems = saleDetails.items.map(item => item.copy(id = None, sale = saleId))
        val saveSoldItems = SoldItemsRepository.saveAll(newSoldItems)

        saveSoldItems.flatMap { soldItemsId =>
          val items = for ( (i, s) <- (soldItemsId zip newSoldItems) ) yield s.copy(id = Some(i))
          DBIOAction.successful( DetailedSale(savedSale.get, items) )
        }
      }
    }

    def updateItemStock( soldItem: SoldItem ): DBIO[Int] ={
      soldItem.fractionId match {
        case None =>
          sqlu"UPDATE items_stock SET stock_count=stock_count - ${soldItem.count} WHERE id=${soldItem.itemId.id};"
        case Some(fractionId) =>
          sqlu"UPDATE items_stock SET stock_count=(stock_count - (SELECT CAST(${soldItem.count} AS NUMERIC(10,2))/qty_in_pack FROM fractions WHERE id=${fractionId.id})) WHERE id=${soldItem.itemId.id};"
      }
    }

    val updateStockAction = DBIOAction.sequence(saleDetails.items.map( updateItemStock ))

    val updateStockAndSaveSale = updateStockAction.andThen( saveSalesAction ).transactionally

    dbConfig.db.run(updateStockAndSaveSale)
  }
}
