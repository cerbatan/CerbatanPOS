package repositories

import models.{ProductBrief, Product}
import models.db._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import repositories._

object ProductsRepository {
  def findById(id: ItemId)(implicit session: Session): Option[Product] = {
    val maybeItem: Option[Item] = ItemsRepository.findById(id)

    if (maybeItem.nonEmpty) {
      val item: Item = maybeItem.get

      val itemBrand = item.brand.map(brandId => BrandsRepository.findById(brandId).get)
      val itemTags = TagsRepository.getTagsForItem(id)

      val stock = ItemsStockRepository.findByItemId(id).get
      val tax = stock.tax.map(taxId => TaxesRepository.findById(taxId).get)

      val itemFractions = FractionsRepository.findByItemId(id)

      return Some(Product(item.id, item.sku, item.name, itemBrand, itemTags,
        stock.cost, stock.price, tax, stock.retailPrice, stock.trackStock, stock.stockCount, stock.alertLowStock, stock.alertStockLevel,
        itemFractions))

    }

    None
  }

  def getBriefs(filter: Option[String], pageNumber: Int, pageSize: Int)(implicit session: Session): List[ProductBrief]  = {
    val offset = if (pageNumber>0) (pageNumber - 1) * pageSize else 1*pageSize

    val briefsQuery = filter match {
      case None => {
        (for{
          ((item, brand), stock) <- (itemsQuery leftJoin  brandsQuery on (_.brand === _.id)) leftJoin itemsStockQuery on(_._1.id === _.item)
        } yield (item.id, item.name, item.sku, brand.name.?, stock.retailPrice, stock.stockCount))
      }

      case Some(f) => {
        (for{
          ((item, brand), stock) <- (itemsQuery leftJoin  brandsQuery on (_.brand === _.id)) leftJoin itemsStockQuery on(_._1.id === _.item)
          if item.name.toLowerCase like s"%${f.toLowerCase()}%"
        } yield (item.id, item.name, item.sku, brand.name.?, stock.retailPrice, stock.stockCount))
      }
    }

    val partialBriefs = briefsQuery.drop(offset).take(pageSize).list

    val briefs = partialBriefs.map{ case (itemId, itemName, sku, brandName, retailPrice, stockCount) => {
      val tags = TagsRepository.getTagsForItem(itemId).map(_.name)
      ProductBrief(itemId, itemName, sku, brandName, tags, retailPrice, stockCount)
    }}

    briefs
  }

  def save(p: Product)(implicit session: Session): ItemId = {
    session.withTransaction {
      val brandId = p.brand.fold[Option[BrandId]](None)(b => b.id)
      val newItem: Item = Item(None, p.sku, p.name, brandId)

      val itemId = ItemsRepository.save(newItem)

      val taxId = p.tax.fold[Option[TaxId]](None)(t => t.id)
      val itemStock = ItemStock(None, itemId, p.cost, p.price, taxId, p.retailPrice, p.trackStock, p.stockCount, p.alertLowStock, p.alertStockLevel)

      p.tags.foreach(t => {
        tagsForItemQuery insert (t.id.get -> itemId)
      })

      p.fractions.foreach(f => {
        FractionsRepository.save(f.copy(item = Some(itemId)))
      })

      ItemsStockRepository.save(itemStock)

      itemId
    }
  }
}
