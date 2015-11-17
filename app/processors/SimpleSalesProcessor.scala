package processors

import java.time.LocalDateTime

import models.SaleDetails
import models.db.{DetailedSale, Sale, SoldItem}
import processors.ResponseStatus.{ResponseStatus, Success}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class SimpleSalesProcessorResponse(status: ResponseStatus, errorString: Option[String], invoiceId: Option[Long]) extends SalesProcessorResponse

class SimpleSalesProcessor extends SalesProcessor {


  override def receive(saleDetails: SaleDetails)(implicit dbConfig: DatabaseConfig[JdbcProfile]) = {
//    val (status, errorDescription) = validateItems(saleDetails.items)
//
//    status match {
//      case Success => {
//        persistSale(saleDetails, dbConfig).map(detailedSale => SimpleSalesProcessorResponse(status, errorDescription, Some(detailedSale.sale.invoiceNumber)))
//      }
//      case error => Future.successful(SimpleSalesProcessorResponse(status, errorDescription, None))
//    }

    Future.successful(SimpleSalesProcessorResponse(Success, None, None))
  }

  def validateItems(items: Seq[SoldItem]): (ResponseStatus, Option[String]) = {
    (Success, None)
  }

  def persistSale(saleDetails: SaleDetails, dbConfig: DatabaseConfig[JdbcProfile]): Future[DetailedSale] = {
    val sale = Sale(None, LocalDateTime.now(), 1)

    Future.successful(DetailedSale(sale, Seq.empty[SoldItem]))
  }
}
