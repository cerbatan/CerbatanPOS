package controllers.products

import controllers.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import models.Role.{Administrator, Seller}
import models.db.{BrandId, Brand}
import play.api.db.slick._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{BodyParsers, Action, Controller}
import repositories.BrandsRepository
import play.api.Play.current

object Products extends Controller with AuthElement with AuthConfiguration {
  implicit val brandWrites: Writes[Brand] = (
    (__ \ "id").write[Option[BrandId]] and
      (__ \ "name").write[String]
    )(unlift(Brand.unapply))

  def products = StackAction(AuthorityKey -> Seller) { implicit request =>
    val user = loggedIn
    Ok(views.html.products.products())
  }

  def newProduct = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    Ok(views.html.products.productForm("Add Product"))
  }

  def brands(query: Option[String]) = StackAction(AuthorityKey -> Seller) { implicit request =>
    DB.withSession { implicit session: Session =>
      val brands = query match {
        case None =>
          BrandsRepository.findAll()
        case Some(search) =>
          BrandsRepository.filter(search)
      }

      Ok(Json.toJson(brands))
    }
  }

  def addBrand() = StackAction(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    DB.withSession { implicit session: Session =>
      val nameResult = request.body.\("name").validate[String]

      nameResult.fold(
        error => {
          BadRequest
        },
        name => {
          BrandsRepository.findByName(name) match {
            case None =>
              val id = BrandsRepository.save(Brand(None, name))
              Ok(Json.toJson(Brand(Some(id), name)))
            case Some(brand) =>
              Ok(Json.toJson(brand))
          }
        }
      )
    }
  }

}
