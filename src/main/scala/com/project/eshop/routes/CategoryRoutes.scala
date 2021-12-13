package com.project.eshop.routes

import cats.effect.kernel.Async
import cats.syntax.all._
import com.project.eshop.domain.{ProductDTO, User}
import com.project.eshop.http.auth.{AuthorizationMiddleware, SecuredRequestHandler}
import com.project.eshop.routes.dto.CategoryDTO
import com.project.eshop.service.CategoryService
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import org.http4s.FormDataDecoder.formEntityDecoder
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl

object CategoryRoutes {

  def of[F[_]: Async](service: CategoryService[F], auth: SecuredRequestHandler[F, User]): AppRoutes[F] =
    new AppRoutes[F] with Http4sDsl[F] {
    override def prefixPath: String = "category"

      val adminOnlyEndpoints: AuthEndpoint[F] = {
        case req @ POST -> Root as _ =>
          for {
            category <- req.req.as[CategoryDTO]
            res <- service.create(category).flatMap(Created(_))
          } yield res
      }

      val endpoints: HttpRoutes[F] = HttpRoutes.of {
        case GET -> Root =>
          for {
            res <- service.get.flatMap(Ok(_))
          } yield res
      }

    override protected def serviceRoutes: HttpRoutes[F] =
      endpoints <+> auth.liftService(AuthorizationMiddleware.adminOnly(adminOnlyEndpoints))
  }

}
