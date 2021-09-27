package com.project.eshop.appuser

import cats.effect.Sync
import cats.syntax.all._
import doobie.implicits._
import doobie.util.transactor.Transactor
import com.project.eshop.codecs.UserCodecs._

trait UserRepo[F[_]] {
  def getUsers: F[List[User]]
  def addUser(user: User): F[Unit]
}

object UserRepo {
  def of[F[_]: Sync](transactor: Transactor[F]): F[UserRepo[F]] = Sync[F].delay(new UserRepo[F] {
    override def getUsers: F[List[User]] =
      sql"SELECT * FROM employee"
        .query[User]
        .to[List]
        .transact(transactor)

    override def addUser(user: User): F[Unit] =
      sql"INSERT INTO employee (username, email) VALUES (${user.name}, ${user.email})"
        .update
        .run
        .transact(transactor)
        .void
  })
}
