package repositories

import models.{ProductBrief, Product}
import models.db._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

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

  def getBriefs(filter: Option[String], pageNumber: Int, pageSize: Int)(implicit session: Session): (Int, List[ProductBrief])  = {
    val offset = if (pageNumber>0) (pageNumber - 1) * pageSize else 1*pageSize

    val query = briefsQuery(filter)

    val totalBriefs = Query(query.length).first
    val partialBriefs = query.drop(offset).take(pageSize).list

    val briefs = partialBriefs.map{ case (itemId, itemName, sku, brandName, retailPrice, stockCount) => {
      val tags = TagsRepository.getTagsForItem(itemId).map(_.name)
      ProductBrief(itemId, itemName, sku, brandName, tags, retailPrice, stockCount)
    }}

    (totalBriefs, briefs)
  }

  private def briefsQuery(filter: Option[String]) = {
    (filter match {
      case None => {
        (for {
          ((item, stock), brand) <- (itemsQuery innerJoin itemsStockQuery on (_.id === _.item)) leftJoin brandsQuery on (_._1.brand === _.id)
        } yield (item.id, item.name, item.sku, brand.name.?, stock.retailPrice, stock.stockCount))
      }

      case Some(f) => {
        (for {
          ((item, stock), brand) <- (itemsQuery innerJoin itemsStockQuery on (_.id === _.item)) leftJoin brandsQuery on (_._1.brand === _.id)
          if item.name.toLowerCase like s"%${f.toLowerCase()}%"
        } yield (item.id, item.name, item.sku, brand.name.?, stock.retailPrice, stock.stockCount))
      }
    }).sortBy(_._2.asc)
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

  def update(p: Product)(implicit session: Session): ItemId = {
    session.withTransaction {
      val brandId = p.brand.fold[Option[BrandId]](None)(b => b.id)
      val newItem: Item = Item(p.id, p.sku, p.name, brandId)

      val itemId = ItemsRepository.save(newItem)

      val taxId = p.tax.fold[Option[TaxId]](None)(t => t.id)
      val itemStock = ItemStock(None, itemId, p.cost, p.price, taxId, p.retailPrice, p.trackStock, p.stockCount, p.alertLowStock, p.alertStockLevel)

      val stock = for { s <- itemsStockQuery if s.item === newItem.id } yield  s
      stock.update(itemStock)


      tagsForItemQuery.filter(_.item === newItem.id).delete

      p.tags.foreach(t => {
        tagsForItemQuery insert (t.id.get -> itemId)
      })

      p.fractions.foreach(f => {
        FractionsRepository.save(f.copy(item = Some(itemId)))
      })

      itemId
    }
  }
}
