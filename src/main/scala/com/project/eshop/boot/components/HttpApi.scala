package com.project.eshop.boot.components

import cats.effect.kernel.Async
import cats.implicits.toSemigroupKOps
import com.project.eshop.routes.RegistrationRoutes
import com.project.eshop.service.UserService
import org.http4s
import org.http4s.HttpRoutes
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import org.http4s.server.middleware.{CORS, Logger}

import scala.util.chaining._

object HttpApi {
  def of[F[_]: Async](services: ServiceModule[F]): http4s.HttpApp[F] =
    of(services.userService)

  def of[F[_]: Async](
    userService: UserService[F],
  ): http4s.HttpApp[F] = {
    val routes = new AppRoutes[F](userService).routes.map(_.routes())
    of(routes.head, routes.tail: _*)
  }

  def of[F[_]: Async](
    first: HttpRoutes[F],
    other: HttpRoutes[F]*
  ): http4s.HttpApp[F] = {
    CORS(
      (first +: other)
        .reduceLeft(_ <+> _)
        .pipe(routes => Router("v1" -> routes))
        .pipe(routes => Router("api" -> routes))
    )
      .orNotFound
      .pipe(Logger.httpApp(logHeaders = true, logBody = true))
  }
}

private final case class AppRoutes[F[_]: Async](
  userService: UserService[F]
) {
  private val registrationRoutes = RegistrationRoutes.of(userService)

  val routes = Seq(registrationRoutes)
}
