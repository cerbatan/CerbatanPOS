import models.db.SystemUsers

import scala.slick.lifted.TableQuery

package object repositories {
  lazy val systemUsersQuery: TableQuery[SystemUsers] = TableQuery[SystemUsers]

  class BasicRepository[E <: scala.slick.lifted.AbstractTable[_]](protected val query: TableQuery[E]);
}
