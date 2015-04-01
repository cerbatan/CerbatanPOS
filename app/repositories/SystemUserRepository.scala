package repositories

import models.db.{SystemUser, SystemUsers, UserId}
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

trait SystemUsersRepositories {
  lazy val systemUsersQuery: TableQuery[SystemUsers] = TableQuery[SystemUsers]

  object SystemUserRepository extends BaseIdRepository[UserId, SystemUser, SystemUsers](systemUsersQuery) {
//https://github.com/VirtusLab/unicorn/blob/v0.6.x-slick-2.1.x/unicorn-core/src/test/scala/org/virtuslab/unicorn/repositories/UsersRepositoryTest.scala
  }

}