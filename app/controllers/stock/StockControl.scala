package controllers.stock

import javax.inject.Inject

import controllers.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import models.Role.Seller
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.Controller
import repositories.SystemUserRepository

class StockControl @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                             val systemUsers: SystemUserRepository) extends Controller with AuthElement with AuthConfiguration {
  def stockControl = StackAction(AuthorityKey -> Seller) { implicit request =>
    Ok(views.html.stock.stockControl())
  }

}
