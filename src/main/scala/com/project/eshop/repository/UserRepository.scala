package com.project.eshop.repository

import com.project.eshop.auth.User
import com.project.eshop.routes.dto.UserDTO.CreateUser
import doobie.implicits._
import doobie.ConnectionIO

private object UserSQL {
  val UserColumns = Seq("id", "login", "email", "last_name", "first_name")

  def insert(user: CreateUser) =
    sql"INSERT INTO users (login, email, password, role, last_name, first_name) VALUES " ++
      sql"(${user.login}, ${user.email}, ${user.password}, 2, ${user.lastName}, ${user.firstName})"

  def select(userId: String) =
    sql"SELECT id, login, email, role, last_name, first_name FROM users WHERE id = $userId"

  def selectLogins = sql"SELECT login FROM users"

  def selectEmails = sql"SELECT email FROM users"
}

trait UserRepository[F[_]] {
  def create(user: CreateUser): F[User]

  def select(userId: String): F[Option[User]]

  def selectLogins: F[List[String]]

  def selectEmails: F[List[String]]
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
  }
}
