package processors

import models.SaleDetails
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future

object ResponseStatus extends Enumeration {
  type ResponseStatus = Value
  val Success = Value(0)
  val Error = Value(1)
}

trait SalesProcessorResponse {
  def status: ResponseStatus.ResponseStatus

  def errorString: Option[String]

  def invoiceId: Option[Long]
}

trait SalesProcessor {
  def receive(saleDetails: SaleDetails)(implicit dbConfig: DatabaseConfig[JdbcProfile]): Future[SalesProcessorResponse]
}
