package controllers.products

import controllers.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import models.Product
import models.Role.{Administrator, Seller}
import models.db.{ItemId, Tax, Tag, Brand}
import org.h2.jdbc.JdbcSQLException
import play.api.Play.current
import play.api.db.slick._
import play.api.libs.json._
import play.api.mvc.{BodyParsers, Controller}
import repositories.{ProductsRepository, TaxesRepository, BrandsRepository, TagsRepository}
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

  def editProduct = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn

    Ok(views.html.products.productForm("Edit Product"))
  }

  def productDetail = StackAction(AuthorityKey -> Seller) { implicit request =>
    val user = loggedIn
    Ok(views.html.products.productDetails())
  }

  def saveProduct = StackAction(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    DB.withSession { implicit session: Session =>
      val user = loggedIn
      operateProduct(request.body, ProductsRepository.save)
    }
  }

  def updateProduct = StackAction(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    DB.withSession { implicit session: Session =>
      val user = loggedIn
      operateProduct(request.body, ProductsRepository.update)
    }
  }

  private def operateProduct(body: JsValue, op: (Product) => ItemId) = {
    val product = body.validate[Product]
    product.fold(
      error => {
        BadRequest
      },
      product => {
        try {
          val id = op(product)
          Ok(Json.toJson(id))
        } catch {
          case e: JdbcSQLException => Conflict
          case e: Exception => InternalServerError
        }

      }
    )
  }

  def getProduct(id: Long) = StackAction(AuthorityKey -> Seller) { implicit request =>
    DB.withSession { implicit session: Session =>
      val itemId = ItemId(id)
      val product: Option[Product] = ProductsRepository.findById(itemId)

      Ok(Json.toJson(product))
    }
  }

  def getProductsBrief(filter: Option[String], page: Option[Int]) = StackAction(AuthorityKey -> Seller) { implicit request =>
    DB.withSession { implicit session: Session =>
      val briefs = ProductsRepository.getBriefs(filter, page.getOrElse(1), 8)
      Ok(Json.toJson(briefs))
    }
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

  def taxes = StackAction(AuthorityKey -> Seller) { implicit request =>
    DB.withSession { implicit session: Session =>
      val tags = TaxesRepository.findAll()

      Ok(Json.toJson(tags))
    }
  }

  def addTax = StackAction(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    DB.withSession { implicit session: Session =>
      val newTax = request.body.validate[Tax]

      newTax.fold(
        error => {
          BadRequest
        },
        tax => {
          val id = TaxesRepository.save(tax)
          Ok(Json.toJson(tax.copy(id = Some(id))))
        }
      )
    }
  }

}
