package models

import models.db._

case class Product(id: Option[ItemId],
                   sku: String, name: String, brand: Option[Brand],
                   tags: Seq[Tag],
                   cost: Double, price: Double, tax: Option[Tax], retailPrice: Double,
                   trackStock: Boolean, stockCount: Float, alertLowStock: Boolean, alertStockLevel: Float, fractions: Seq[Fraction])

case class ProductBrief(id: ItemId,
                        name: String, sku: String, brand: Option[String],
                        tags: Seq[String], retailPrice: Double, stockCount: Float)

case class ListedProduct(id: ItemId, sku: String, name: String)