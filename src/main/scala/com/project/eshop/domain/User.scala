package com.project.eshop.domain

import cats.Applicative
import cats.syntax.all._
import com.project.eshop.domain.Role.Admin
import tsec.authorization.AuthorizationInfo

case class User(
  id: String,
  username: String,
  email: String,
  role: String,
  firstName: String,
  lastName: String
)

object User {
  implicit def authRole[F[_]: Applicative]: AuthorizationInfo[F, Role, User] =
    (u: User) =>
      (if (u.role == "admin") Admin
       else Role.User).pure
}
