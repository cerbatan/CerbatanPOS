package controllers.products

import java.sql.SQLException
import javax.inject.Inject

import common.format.products._
import controllers.AuthConfiguration
import jp.t2v.lab.play2.auth.AuthElement
import models.Product
import models.Role.{Administrator, Seller}
import models.db.{Brand, ItemId, Tag, Tax}
import play.api.db.slick._
import play.api.libs.json._
import play.api.mvc.{BodyParsers, Controller, Result}
import repositories._
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Products @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                         val systemUsers: SystemUserRepository,
                         val productsRepository: ProductsRepository) extends Controller with AuthElement with AuthConfiguration {

  def products = StackAction(AuthorityKey -> Seller) { implicit request =>
    val user = loggedIn
    Ok(views.html.products.products())
  }

  def newProduct = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn

    Ok(views.html.products.productForm("New Product"))
  }

  def editProduct = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn

    Ok(views.html.products.productForm("Edit Product"))
  }

  def productDetail = StackAction(AuthorityKey -> Seller) { implicit request =>
    val user = loggedIn
    Ok(views.html.products.productDetails())
  }

  def saveProduct = AsyncStack(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    operateProduct(request.body, productsRepository.save)
  }

  def updateProduct = AsyncStack(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    operateProduct(request.body, productsRepository.update)
  }

  private def operateProduct(body: JsValue, op: (Product) => DBIO[ItemId]): Future[Result] = {
    val product = body.validate[Product]
    product.fold(
      error => {
        Future.successful(BadRequest)
      },
      product => {
        db.run(op(product)).map(itemId => Ok(Json.toJson(itemId))).recover({
          case e: SQLException => Conflict
          case e: Exception => InternalServerError
        })
      }
    )
  }

  def getProduct(id: Long) = AsyncStack(AuthorityKey -> Seller) { implicit request =>
    val itemId = ItemId(id)
    db.run(productsRepository.findById(itemId).map(product => Ok(Json.toJson(product))))
  }

  def getProductsBrief(filter: Option[String], page: Option[Int]) = AsyncStack(AuthorityKey -> Seller) { implicit request =>
    val getBriefsAction = productsRepository.getBriefs(filter, page.getOrElse(1), 8)

    db.run(getBriefsAction).map(briefsResult => Ok(Json.toJson(briefsResult)))
  }

  def brands(query: Option[String]) = AsyncStack(AuthorityKey -> Seller) { implicit request =>
    val brandsQueryAction = query match {
      case None =>
        BrandsRepository.findAll()
      case Some(search) =>
        BrandsRepository.filter(search)
    }

    db.run(brandsQueryAction.map(brands => Ok(Json.toJson(brands))))
  }

  def addBrand = AsyncStack(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    val nameResult = request.body.\("name").validate[String]

    nameResult.fold(
      error => {
        Future.successful(BadRequest)
      },
      name => {
        val fetchOrSaveBrandAction = BrandsRepository.findByName(name).flatMap(maybeBrand => maybeBrand match {
          case None =>
            BrandsRepository.save(Brand(None, name)).map(id => Brand(Some(id), name))
          case Some(brand) =>
            DBIO.successful(brand)
        })

        db.run(fetchOrSaveBrandAction.map(brand => Ok(Json.toJson(brand))))
      }
    )
  }

  def tags = AsyncStack(AuthorityKey -> Seller) { implicit request =>
    db.run(TagsRepository.findAll().map(tags => Ok(Json.toJson(tags))))
  }


  def addTag = AsyncStack(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    val nameResult = request.body.\("name").validate[String]

    nameResult.fold(
      error => {
        Future.successful(BadRequest)
      },
      name => {
        val saveOrFetchTag: DBIO[Tag] = TagsRepository.findByName(name).flatMap(maybeTag => maybeTag match {
          case None =>
            TagsRepository.save(Tag(None, name)).map(tagId => Tag(Some(tagId), name))
          case Some(tag) =>
            DBIO.successful(tag)
        })

        db.run(saveOrFetchTag.map(tag => Ok(Json.toJson(tag))))
      }
    )
  }

  def taxes = AsyncStack(AuthorityKey -> Seller) { implicit request =>
    db.run(TaxesRepository.findAll()).map(t => Ok(Json.toJson(t)))
  }


  def addTax = AsyncStack(BodyParsers.parse.json, AuthorityKey -> Administrator) { implicit request =>
    val newTax = request.body.validate[Tax]

    newTax.fold(
      error => {
        Future.successful(BadRequest)
      },
      tax => {
        val addTaxAction = TaxesRepository.save(tax).map(id => tax.copy(id = Some(id)))

        db.run(addTaxAction).map(addedTax => Ok(Json.toJson(addedTax)))
      }
    )
  }

}
