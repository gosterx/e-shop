package com.project.eshop

import cats.effect.{ExitCode, IO, IOApp}
import com.project.eshop.appuser.{UserController, UserRepo}
import com.project.eshop.db.DBTransactor
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.middleware._

import scala.concurrent.ExecutionContext

object AppRunner extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    DBTransactor
      .make[IO]
      .use { tx =>
        for {
          userRepo <- UserRepo.of[IO](tx)
          _        <- httpServer(routers(userRepo))
        } yield ()
      }
      .as(ExitCode.Success)

  def routers(userRepo: UserRepo[IO]): HttpApp[IO] = {
    val originConfig = CORSConfig.default.withAnyOrigin(true)
    CORS(new UserController[IO](userRepo).routes, originConfig).orNotFound
  }

  def httpServer(routes: HttpApp[IO]): IO[Unit] =
    BlazeServerBuilder[IO](ExecutionContext.global)
      .bindHttp(port = 9001, host = "localhost")
      .withHttpApp(routes)
      .serve
      .compile
      .drain
}
