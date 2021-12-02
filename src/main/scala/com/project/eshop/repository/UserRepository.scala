package com.project.eshop.repository

import com.project.eshop.domain.{User, UserWithAuthInfo}
import com.project.eshop.routes.dto.UserDTO.{CreateUser, UserAuthInfo}
import doobie.implicits._
import doobie.ConnectionIO
import doobie.util.fragment

private object UserSQL {
  val UserColumns = Seq("id", "username", "email", "role", "first_name", "last_name")

  def insert(user: CreateUser): fragment.Fragment =
    sql"INSERT INTO users (username, email, password, first_name, last_name) VALUES " ++
      sql"(${user.username}, ${user.email}, ${user.password}, ${user.firstName}, ${user.lastName})"

  def select(userId: String) =
    sql"SELECT id, login, email, role, last_name, first_name FROM users WHERE id = $userId"

  def selectLogins = sql"SELECT username FROM users"

  def selectEmails = sql"SELECT email FROM users"

  def selectUserAuthInfoByUsername(username: String) = sql"SELECT id, username, password FROM users WHERE username = $username"
}

trait UserRepository[F[_]] {
  def create(user: CreateUser): F[User]

  def select(userId: String): F[Option[User]]

  def selectLogins: F[List[String]]

  def selectEmails: F[List[String]]

  def selectUserAuthInfoByUsername(login: String): F[Option[UserWithAuthInfo]]
}

object UserRepository {
  def make: UserRepository[ConnectionIO] = new UserRepository[ConnectionIO] {
    override def create(user: CreateUser): ConnectionIO[User] =
      UserSQL.insert(user).update.withUniqueGeneratedKeys[User](UserSQL.UserColumns: _*)

    override def select(userId: String): ConnectionIO[Option[User]] =
      UserSQL.select(userId).query[User].option

    override def selectLogins: ConnectionIO[List[String]] =
      UserSQL.selectLogins.query[String].to[List]

    override def selectEmails: ConnectionIO[List[String]] =
      UserSQL.selectEmails.query[String].to[List]

    override def selectUserAuthInfoByUsername(login: String): ConnectionIO[Option[UserWithAuthInfo]] =
      UserSQL.selectUserAuthInfoByUsername(login).query[UserWithAuthInfo].option
  }
}
