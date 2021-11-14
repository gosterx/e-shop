package com.project.eshop.boot.components

import cats.effect.{Async, Resource}
import com.project.eshop.db.DBConfig.{dbDriverName, dbPwd, dbUrl, dbUser}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

object RepositoryModule {

  def make[F[_]: Async]: Resource[F, Transactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](10)
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = dbDriverName,
        url             = dbUrl,
        user            = dbUser,
        pass            = dbPwd,
        connectEC       = ce // await connection on this EC
      )
    } yield xa
}
