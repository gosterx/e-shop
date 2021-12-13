package com.project.eshop.routes

import cats.effect.kernel.Async
import cats.syntax.all._
import com.project.eshop.domain.User
import com.project.eshop.http.auth.{AuthorizationMiddleware, SecuredRequestHandler}
import com.project.eshop.routes.dto.OrderInfo
import com.project.eshop.service.CheckoutService
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import org.http4s.FormDataDecoder.formEntityDecoder
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl

object CheckoutRoutes {

  def of[F[_]: Async](service: CheckoutService[F], auth: SecuredRequestHandler[F, User]): AppRoutes[F] =
    new AppRoutes[F] with Http4sDsl[F] {
      override def prefixPath: String = "/checkout"

      val endpoints: AuthEndpoint[F] = {
        case req @ POST -> Root as user =>
          for {
            req <- req.req.as[OrderInfo]
            res <- service.checkout(req).flatMap(Ok(_))
          } yield res
      }

      val adminOnlyEndpoints: AuthEndpoint[F] = {
        case GET -> Root / "orders" as user =>
          for {
            res <- service.getOrders.flatMap(Ok(_))
          } yield res
      }

      override protected def serviceRoutes: HttpRoutes[F] = {
        val routes = AuthorizationMiddleware.allRolesHandler(
          endpoints,
        ) {
          AuthorizationMiddleware.adminOnly(adminOnlyEndpoints)
        }
        auth.liftService(routes)
      }
    }

}
