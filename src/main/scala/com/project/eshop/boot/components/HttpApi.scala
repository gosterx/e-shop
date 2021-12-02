package com.project.eshop.boot.components

import cats.effect.kernel.Async
import cats.implicits.toSemigroupKOps
import cats.syntax.all._
import com.project.eshop.domain.User
import com.project.eshop.http.auth.SecuredRequestHandler
import com.project.eshop.routes.{LoginRoutes, RegistrationRoutes}
import com.project.eshop.service.{LoginService, UserService}
import org.http4s
import org.http4s.HttpRoutes
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import org.http4s.server.middleware.{CORS, CORSConfig, Logger}

import scala.util.chaining._

object HttpApi {
  def of[F[_]: Async](auth: SecuredRequestHandler[F, User], services: ServiceModule[F]): http4s.HttpApp[F] =
    of(auth, services.userService, services.loginService)

  def of[F[_]: Async](
    auth: SecuredRequestHandler[F, User],
    userService: UserService[F],
    loginService: LoginService[F]
  ): http4s.HttpApp[F] = {
    val routes = new AppRoutes[F](auth, userService, loginService).routes
    of(routes)
  }

  def of[F[_]: Async](
    first: HttpRoutes[F],
    other: HttpRoutes[F]*
  ): http4s.HttpApp[F] = {

    CORS(
      (first +: other)
        .reduceLeft(_ <+> _)
        .orNotFound
        .pipe(Logger.httpApp(logHeaders = true, logBody = true))
    )
  }
}

private final case class AppRoutes[F[_]: Async](
  auth: SecuredRequestHandler[F, User],
  userService: UserService[F],
  loginService: LoginService[F]
) {
  private val registrationRoutes = RegistrationRoutes.of(userService, auth)
  private val loginRoutes        = LoginRoutes.of(loginService, auth)

  val routes: HttpRoutes[F] = Seq(registrationRoutes, loginRoutes)
    .map(_.routes())
    .reduceLeft(_ <+> _)
    .pipe(routes => Router("v1" -> routes))
    .pipe(routes => Router("api" -> routes))
}
