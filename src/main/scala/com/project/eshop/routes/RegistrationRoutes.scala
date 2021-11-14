package com.project.eshop.routes

import cats.effect.kernel.Async
import cats.syntax.all._
import com.project.eshop.service.{EmailAlreadyUse, LoginAlreadyUse, UserService}
import com.project.eshop.codecs.Codecs._
import com.project.eshop.routes.dto.UserDTO.CreateUser
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._

object RegistrationRoutes {

  def of[F[_]: Async](service: UserService[F]): AppRoutes[F] = new AppRoutes[F] with Http4sDsl[F] {

    override def prefixPath: String = "registration"

    override protected def notAuthRoutes: HttpRoutes[F] = HttpRoutes.of {
      case req @ POST -> Root =>
        (for {
          userDTO <- req.as[CreateUser]
          res <- service.create(userDTO).flatMap(Created(_))
        } yield res).handleErrorWith {
          case LoginAlreadyUse => BadRequest(LoginAlreadyUse.getMessage)
          case EmailAlreadyUse => BadRequest(EmailAlreadyUse.getMessage)
        }
    }

  }
}
