package com.project.eshop.repository

import cats.effect.unsafe.implicits.global
import cats.effect.IO
import doobie.util.transactor.Transactor
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.project.eshop.db.LiquibaseMigration
import com.project.eshop.domain.User
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}

trait DbSetup extends Suite with BeforeAndAfterAll with BeforeAndAfterEach{
  private var postgres: EmbeddedPostgres = _
  val username                           = "postgres"
  val dbName                             = "postgres"
  val password                           = "postgres"
  var dbUrl                              = s"jdbc:postgresql://localhost:5432/$dbName"
  var transactor: Transactor[IO]         = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    postgres = EmbeddedPostgres.builder().setPort(5432).start()
    dbUrl    = postgres.getJdbcUrl(username, dbName)
    println(dbUrl)
    LiquibaseMigration.run[IO](dbUrl, username, password).unsafeRunSync()
    transactor = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      dbUrl,
      username,
      password
    )
  }

  override protected def afterAll(): Unit = {
    postgres.close()
    super.afterAll()
  }
}
