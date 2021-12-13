package com.project.eshop.routes

import cats.effect
import cats.effect.Concurrent
import cats.effect.kernel.{Async, Sync}
import cats.syntax.all._
import com.project.eshop.domain.{ProductDTO, User}
import com.project.eshop.domain.ProductDTO._
import com.project.eshop.http.auth.{AuthorizationMiddleware, SecuredRequestHandler}
import com.project.eshop.service.ProductService
import com.project.eshop.service.errors.NoSuchProduct
import io.circe.generic.auto.exportEncoder
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object ProductRoutes {
  def of[F[_]: Async: Concurrent](service: ProductService[F], auth: SecuredRequestHandler[F, User]): AppRoutes[F] =
    new AppRoutes[F] with Http4sDsl[F] {
      override def prefixPath: String = "product"

      val adminOnlyEndpoints: AuthEndpoint[F] = {
        case req @ POST -> Root as _ =>
          for {
            product <- req.req.as[ProductDTO]
            res     <- service.create(product).flatMap(Created(_))
          } yield res
      }

      val endpoints: HttpRoutes[F] = HttpRoutes.of {
        case GET -> Root =>
          for {
            res <- service.getAllProducts().flatMap(Ok(_))
          } yield res

        case GET -> Root / category =>
          for {
            products <- service.getProductsWithCategory(category)
            res <- products match {
              case Nil => NoContent()
              case _   => Ok(products)
            }
          } yield res

        case GET -> Root / "find" / id =>
          (for {
            _ <- effect.Sync[F].delay("12314")
            res <- service.getProductById(id).map(_ => println(service.getProductById(id))).flatMap(_ => Ok(service.getProductById(id)))
          } yield res).handleErrorWith {
            case NoSuchProduct => NoContent()
          }
      }

      override protected def serviceRoutes: HttpRoutes[F] = {
        endpoints <+> auth.liftService(AuthorizationMiddleware.adminOnly(adminOnlyEndpoints))
      }
    }
}
