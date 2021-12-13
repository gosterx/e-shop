package com.project.eshop.repository

import com.project.eshop.domain.User
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator

object TokenSQL {
  def upsert(userId: String, token: String) =
    sql"INSERT INTO tokens (user_id, token) VALUES (CAST($userId as int), $token) ON CONFLICT (user_id) " ++
      sql"DO UPDATE SET token = $token WHERE tokens.user_id = CAST($userId as int)"

  def getUser(token: String) =
    sql"SELECT id, username, email, role, first_name, last_name FROM users u JOIN tokens t ON t.user_id=u.id WHERE t.token=$token"
}

trait TokenRepository[F[_]] {
  def upsert(userId: String, token: String): ConnectionIO[String]

  def getUserByToken(token: String): ConnectionIO[Option[User]]
}

object TokenRepository {
  def make: TokenRepository[ConnectionIO] = new TokenRepository[ConnectionIO] {
    override def upsert(userId: String, token: String): ConnectionIO[String] =
      TokenSQL.upsert(userId, token).update.withUniqueGeneratedKeys[String]("token")

    override def getUserByToken(token: String): ConnectionIO[Option[User]] =
      TokenSQL.getUser(token).query[User].option
  }
}
