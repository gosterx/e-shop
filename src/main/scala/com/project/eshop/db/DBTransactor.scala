package com.project.eshop.db

import cats.effect.{Async, Resource}
import com.project.eshop.db.DBConfig._
import doobie.hikari.HikariTransactor
import doobie.{ExecutionContexts, Transactor}

object DBTransactor {
  def make[F[_]: Async]: Resource[F, Transactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](10)
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = dbDriverName,
        url = dbUrl,
        user = dbUser,
        pass = dbPwd,
        connectEC = ce // await connection on this EC
      )
    } yield xa
}
