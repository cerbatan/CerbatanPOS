import models.db._
import slick.lifted.TableQuery


package object repositories {
  lazy val systemUsersQuery: TableQuery[SystemUsers] = TableQuery[SystemUsers]
  lazy val brandsQuery: TableQuery[Brands] = TableQuery[Brands]
  lazy val tagsQuery: TableQuery[Tags] = TableQuery[Tags]
  lazy val taxesQuery: TableQuery[Taxes] = TableQuery[Taxes]
  lazy val itemsQuery: TableQuery[Items] = TableQuery[Items]
  lazy val itemsStockQuery: TableQuery[ItemsStock] = TableQuery[ItemsStock]
  lazy val tagsForItemQuery: TableQuery[TagsForItem] = TableQuery[TagsForItem]
  lazy val fractionsQuery: TableQuery[Fractions] = TableQuery[Fractions]
  lazy val salesQuery: TableQuery[Sales] = TableQuery[Sales]
  lazy val soldItemsQuery: TableQuery[SoldItems] = TableQuery[SoldItems]
  lazy val ordersQuery: TableQuery[Orders] = TableQuery[Orders]
  lazy val orderItemsQuery: TableQuery[OrderItems] = TableQuery[OrderItems]

  class BasicRepository[E <: slick.lifted.AbstractTable[_]](protected val query: TableQuery[E]);
}
