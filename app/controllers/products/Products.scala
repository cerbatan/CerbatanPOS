package controllers.products

import controllers.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import models.Role.{Administrator, Seller}
import models.db.{Tag, Brand}
import play.api.Play.current
import play.api.db.slick._
import play.api.libs.json._
import play.api.mvc.{BodyParsers, Controller}
import repositories.{BrandsRepository, TagsRepository}
import common.format.products._

object Products extends Controller with AuthElement with AuthConfiguration {
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

  def addBrand = StackAction(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
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

  def tags = StackAction(AuthorityKey -> Seller) { implicit request =>
    DB.withSession { implicit session: Session =>
      val tags = TagsRepository.findAll()

      Ok(Json.toJson(tags))
    }
  }


  def addTag = StackAction(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    DB.withSession { implicit session: Session =>
      val nameResult = request.body.\("name").validate[String]

      nameResult.fold(
        error => {
          BadRequest
        },
        name => {
          TagsRepository.findByName(name) match {
            case None =>
              val id = TagsRepository.save(Tag(None, name))
              Ok(Json.toJson(Tag(Some(id), name)))
            case Some(tag) =>
              Ok(Json.toJson(tag))
          }
        }
      )
    }
  }

}
