package com.project.eshop.boot.components

import cats.effect.{Async, Resource}
import com.project.eshop.db.DBConfig.{dbDriverName, dbPwd, dbUrl, dbUser}
import com.project.eshop.db.LiquibaseMigration
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

object RepositoryModule {

  def make[F[_]: Async](): Resource[F, Transactor[F]] = for {
    connEc <- ExecutionContexts.fixedThreadPool[F](10)
    txnEc  <- ExecutionContexts.cachedThreadPool[F]
    _      <- Resource.eval(LiquibaseMigration.run(dbUrl, dbUser, dbPwd))
    transactor <- HikariTransactor.newHikariTransactor[F](
      dbDriverName,
      dbUrl,
      dbUser,
      dbPwd,
      connEc
    )
  } yield transactor
}
