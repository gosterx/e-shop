package com.project.eshop.domain

import cats.Eq
import tsec.authorization.{AuthGroup, SimpleAuthEnum}

final case class Role(roleRepr: String)

object Role extends SimpleAuthEnum[Role, String] {
  val AdminRoleName = "admin"
  val UserRoleName = "user"

  val Admin: Role = Role(AdminRoleName)
  val User: Role = Role(UserRoleName)

  override val values: AuthGroup[Role] = AuthGroup(Admin, User)

  override def getRepr(t: Role): String = t.roleRepr

  implicit val eqRole: Eq[Role] = Eq.fromUniversalEquals[Role]
}
