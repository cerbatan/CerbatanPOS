package repositories

import models.Product
import models.db._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._

object ProductsRepository {
  def findById(id: ItemId)(implicit session: Session): Option[Product] = {
    val maybeItem: Option[Item] = ItemsRepository.findById(id)

    if (maybeItem.nonEmpty) {
      val item: Item = maybeItem.get

      val itemBrand = item.brand.map(brandId => BrandsRepository.findById(brandId).get)
      val itemTags = (for {
        ti <- tagsForItemQuery
        t <- tagsQuery if ti.tag === t.id && ti.item === id
      } yield t).list

      val stock = ItemsStockRepository.findByItemId(id).get
      val tax = stock.tax.map(taxId => TaxesRepository.findById(taxId).get)

      val itemFractions = FractionsRepository.findByItemId(id)

      return  Some(Product(item.id, item.sku, item.name, itemBrand, itemTags,
        stock.cost, stock.price, tax, stock.retailPrice, stock.trackStock, stock.stockCount, stock.alertLowStock, stock.alertStockLevel,
      itemFractions))

    }

    None
  }

  def save(p: Product)(implicit session: Session): ItemId = {
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
