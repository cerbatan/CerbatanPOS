import models.db.{Tags, Brands, SystemUsers}

import scala.slick.lifted.TableQuery

package object repositories {
  lazy val systemUsersQuery: TableQuery[SystemUsers] = TableQuery[SystemUsers]
  lazy val brandsQuery: TableQuery[Brands] = TableQuery[Brands]
  lazy val tagsQuery: TableQuery[Tags] = TableQuery[Tags]

  class BasicRepository[E <: scala.slick.lifted.AbstractTable[_]](protected val query: TableQuery[E]);
}
