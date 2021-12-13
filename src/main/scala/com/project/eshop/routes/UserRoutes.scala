package com.project.eshop.routes

import cats.effect.kernel.Async
import cats.syntax.all._
import com.project.eshop.domain.User
import com.project.eshop.codecs.Codecs._
import com.project.eshop.http.auth.{AuthorizationMiddleware, SecuredRequestHandler}
import com.project.eshop.service.UserService
import io.circe.generic.auto.exportEncoder
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl

object UserRoutes {

  def of[F[_]: Async](service: UserService[F], auth: SecuredRequestHandler[F, User]): AppRoutes[F] = new AppRoutes[F] with Http4sDsl[F]{
    override def prefixPath: String = "user"

    val adminOnlyEndpoints: AuthEndpoint[F] = {
      case GET -> Root as user =>
        for {
          res <- service.getUsers.flatMap(Ok(_))
        } yield res
    }

    override protected def serviceRoutes: HttpRoutes[F] =
      auth.liftService(AuthorizationMiddleware.adminOnly(adminOnlyEndpoints))
  }

}
