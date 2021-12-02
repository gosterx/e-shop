package com.project.eshop.db

import cats.effect.{Resource, Sync}
import liquibase.{Contexts, LabelExpression, Liquibase}
import liquibase.database.DatabaseConnection
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import cats.syntax.all._

import java.sql.DriverManager

object LiquibaseMigration {
  private val MasterChangelogFile = "migrations/changelog.xml"

  def run[F[_]: Sync](dbUrl: String, dbUser: String, dbPassword: String): F[Unit] =
    createLiquibase[F](dbUrl, dbUser, dbPassword).use { liquibase =>
      Sync[F].delay {
        val (contexts, label) = (new Contexts(), new LabelExpression)
        if (!liquibase.listUnrunChangeSets(contexts, label).isEmpty) {
          liquibase.update(contexts, label)
        }
      }
    }

  private def createLiquibase[F[_]: Sync](dbUrl: String, dbUser: String, dbPassword: String): Resource[F, Liquibase] =
    for {
      resourceAccessor <- Resource.pure(new ClassLoaderResourceAccessor(getClass.getClassLoader))
      conn             <- createDatabaseConnection(dbUrl, dbUser, dbPassword)
      liquibase <- Resource.make(
        new Liquibase(MasterChangelogFile, resourceAccessor, conn).pure[F]
      )(liquibase => Sync[F].delay(liquibase.forceReleaseLocks()))
    } yield liquibase

  private def createDatabaseConnection[F[_]: Sync](
    dbUrl: String,
    dbUser: String,
    dbPassword: String
  ): Resource[F, DatabaseConnection] =
    Resource.make(
      Sync[F].delay(
        new JdbcConnection(DriverManager.getConnection(dbUrl, dbUser, dbPassword))
      )
    )(conn => Sync[F].delay(conn.close()))

}
