package repositories

import javax.inject.Singleton

import models.db._
import models.{ListedProduct, Product, ProductBrief}
import org.virtuslab.unicorn.LongUnicornPlay.driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ProductsRepository {
  def getListedProducts(query: String): DBIO[Seq[ListedProduct]] = {
    val itemBySkuAction = itemsQuery.filter(_.sku === query).result.headOption

    itemBySkuAction.flatMap(maybeItem => maybeItem match {
      case None =>
        val items = for {
          item <- itemsQuery if item.name.toLowerCase like s"%${query.toLowerCase()}%"
        } yield (item.id.?, item.sku, item.name)

        items.result.map(list => list.map { case (id, sku, name) => ListedProduct(id.getOrElse(ItemId(-1)), sku, name) })

      case Some(item) =>
        DBIO.from(Future.successful(List(ListedProduct(item.id.getOrElse(ItemId(-1)), item.sku, item.name))))
    })

  }

  def findById(id: ItemId): DBIO[Option[Product]] = {
    val findItemAction = ItemsRepository.findById(id)

    findItemAction.flatMap(maybeItem => maybeItem match {
      case None =>
        DBIO.successful(None)
      case Some(item) => {
        val brandIdAction = item.brand match {
          case None =>
            DBIO.successful(None)
          case Some(brandId) =>
            BrandsRepository.findById(brandId)
        }

        brandIdAction.flatMap(
          brand => {
            TagsRepository.getTagsForItem(id).flatMap(
              tags => {
                FractionsRepository.findByItemId(id).flatMap(
                  fractions => {
                    ItemsStockRepository.findByItemId(id).flatMap(
                      maybeStock => maybeStock match {
                        case None =>
                          DBIO.successful(Some(Product(item.id, item.sku, item.name, brand, tags,
                            0, 0, None, 0, false, 0, false, 0, fractions)))
                        case Some(stock) => {
                          stock.tax match {
                            case None =>
                              DBIO.successful(Some(Product(item.id, item.sku, item.name, brand, tags,
                                stock.cost, stock.price, None, stock.retailPrice, stock.trackStock, stock.stockCount, stock.alertLowStock, stock.alertStockLevel,
                                fractions)))
                            case Some(taxId) =>
                              TaxesRepository.findById(taxId).map(tax => Some(Product(item.id, item.sku, item.name, brand, tags,
                                stock.cost, stock.price, tax, stock.retailPrice, stock.trackStock, stock.stockCount, stock.alertLowStock, stock.alertStockLevel,
                                fractions)))
                          }
                        }
                      }

                    )
                  }
                )
              }
            )
          }
        )
      }
    })
  }


  def getBriefs(filter: Option[String], pageNumber: Int, pageSize: Int): DBIO[(Int, Seq[ProductBrief])] = {
    val offset = if (pageNumber > 0) (pageNumber - 1) * pageSize else 1 * pageSize

    val briefsQueryAction = briefsQuery(filter)


    val totalBriefs = briefsQueryAction.length.result
    val partialBriefs = briefsQueryAction.drop(offset).take(pageSize).result

    totalBriefs.flatMap(total => {
      partialBriefs.flatMap(briefTuples => {
        val getTagsActions = briefTuples.map { case (itemId, itemName, sku, brandName, retailPrice, stockCount) => {
          TagsRepository.getTagsForItem(itemId).map(tags => ProductBrief(itemId, itemName, sku, brandName, tags.map(_.name), retailPrice, stockCount))
        }
        }

        DBIO.sequence(getTagsActions)

      }).map(briefs => (total, briefs))
    })
  }

  private def briefsQuery(filter: Option[String]) = {
    (filter match {
      case None => {
        (for {
          ((item, stock), brand) <- (itemsQuery join itemsStockQuery on (_.id === _.item)) joinLeft brandsQuery on (_._1.brand === _.id)
        } yield (item.id, item.name, item.sku, brand.map(_.name), stock.retailPrice, stock.stockCount))
      }

      case Some(f) => {
        (for {
          ((item, stock), brand) <- (itemsQuery join itemsStockQuery on (_.id === _.item)) joinLeft brandsQuery on (_._1.brand === _.id)
          if item.name.toLowerCase like s"%${f.toLowerCase()}%"
        } yield (item.id, item.name, item.sku, brand.map(_.name), stock.retailPrice, stock.stockCount))
      }
    }).sortBy(_._2.asc)
  }

  def save(p: Product): DBIO[ItemId] = {
    val brandId = p.brand.fold[Option[BrandId]](None)(b => b.id)
    val newItem: Item = Item(None, p.sku, p.name, brandId)
    val taxId = p.tax.fold[Option[TaxId]](None)(t => t.id)

    val saveItemAction = ItemsRepository.save(newItem)

    saveItemAction.flatMap(itemId => {
      val itemStock = ItemStock(None, itemId, p.cost, p.price, taxId, p.retailPrice, p.trackStock, p.stockCount, p.alertLowStock, p.alertStockLevel)

      val tagItemTuples = p.tags.map(t => t.id.get -> itemId)
      val fractions = p.fractions.map(fraction => fraction.copy(item = Some(itemId)))

      val insertAllTagsAction = tagItemTuples.toIndexedSeq map tagsForItemQuery.+=

      DBIO.sequence(insertAllTagsAction)
        .andThen(FractionsRepository.saveAll(fractions))
        .andThen(ItemsStockRepository.save(itemStock))
        .andThen(DBIO.successful(itemId))
    }).transactionally
  }


  def update(p: Product): DBIO[ItemId] = {
    val brandId = p.brand.fold[Option[BrandId]](None)(b => b.id)
    val itemToUpdate: Item = Item(p.id, p.sku, p.name, brandId)
    val taxId = p.tax.fold[Option[TaxId]](None)(t => t.id)

    ItemsRepository.save(itemToUpdate).flatMap(itemId => {
      val itemStock = ItemStock(None, itemId, p.cost, p.price, taxId, p.retailPrice, p.trackStock, p.stockCount, p.alertLowStock, p.alertStockLevel)
      val stock = for {s <- itemsStockQuery if s.item === itemToUpdate.id} yield s
      stock.update(itemStock)
        .andThen(tagsForItemQuery.filter(_.item === itemToUpdate.id).delete)
        .andThen(tagsForItemQuery ++= p.tags.map(t => (t.id.get -> itemId)))
        .andThen(FractionsRepository.saveAll(p.fractions.map(f => f.copy(item = Some(itemId)))))
        .andThen(DBIO.successful(itemId))
    }).transactionally
  }
}
