package models

import models.db._

case class Product(id: Option[ItemId],
                   sku: String, name: String, brand: Option[Brand],
                   tags: List[Tag],
                   cost: Double, price: Double, tax: Option[Tax], retailPrice: Double,
                   trackStock: Boolean, stockCount: Float, alertLowStock: Boolean, alertStockLevel: Float, fractions: List[Fraction] )

case class ProductBrief(id: ItemId,
                        name: String, sku: String, brand: Option[String],
                        tags: List[String], retailPrice: Double, stockCount: Float)