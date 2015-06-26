import models.db.{Brands, SystemUsers}

import scala.slick.lifted.TableQuery

package object repositories {
  lazy val systemUsersQuery: TableQuery[SystemUsers] = TableQuery[SystemUsers]
  lazy val brandsQuery: TableQuery[Brands] = TableQuery[Brands]

  class BasicRepository[E <: scala.slick.lifted.AbstractTable[_]](protected val query: TableQuery[E]);
}
