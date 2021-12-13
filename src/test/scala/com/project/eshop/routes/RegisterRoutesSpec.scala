package com.project.eshop.routes

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import com.project.eshop.domain.User
import com.project.eshop.repository.DbSetup
import com.project.eshop.routes.dto.UserDTO.CreateUser
import doobie.implicits._
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import org.http4s.FormDataDecoder.formEntityDecoder
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.{Method, Request, Status, Uri}
import org.http4s.client.Client
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, SECONDS}

class RegisterRoutesSpec extends AnyWordSpec with Matchers with DbSetup {

  private lazy val ec: ExecutionContext     = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(1))
  val client: Resource[IO, HttpService[IO]] = HttpService.of[IO](FiniteDuration(5, SECONDS), ec)

  "Request to register API" should {
    "register user" in {
      val request =
        Request[IO](method = Method.POST, uri = Uri.unsafeFromString("http://localhost:8080/api/v1/registration"))
          .withEntity(
            CreateUser("example", "example@gmail.com", "password", "lastName", "firstName")
          )
      val response = client
        .use(service =>
          for {
            resp <- service.makeRequest(request)
          } yield resp
        )
        .unsafeRunSync()

      response.status shouldBe Status.Created
      response.as[User].unsafeRunSync().copy(id = "1") shouldEqual User(
        "1",
        "example",
        "example@gmail.com",
        "user",
        "firstName",
        "lastName"
      )
    }
  }

  override protected def afterEach(): Unit = {
    sql"TRUNCATE table users cascade".update.run.transact(transactor).unsafeRunSync()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
  }

}
