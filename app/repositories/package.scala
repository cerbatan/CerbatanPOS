import models.Tokens
import models.db.{AuthenticationProfiles, SystemUsers}

import scala.slick.lifted.TableQuery

package object repositories {
  lazy val systemUsersQuery: TableQuery[SystemUsers] = TableQuery[SystemUsers]
  lazy val authenticationProfilesQuery: TableQuery[AuthenticationProfiles] = TableQuery[AuthenticationProfiles]
  lazy val tokensQuery: TableQuery[Tokens] = TableQuery[Tokens]

  class BasicRepository[E <: scala.slick.lifted.AbstractTable[_]](protected val query: TableQuery[E]);
}
