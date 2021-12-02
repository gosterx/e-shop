package com.project.eshop.routes

import cats.effect.kernel.Async
import cats.syntax.all._
import com.project.eshop.domain.User
import com.project.eshop.http.auth.SecuredRequestHandler
import com.project.eshop.routes.dto.UserDTO.{CreateUser, UserAuthInfo}
import com.project.eshop.service.{
  EmailAlreadyUse,
  IncorrectLogin,
  IncorrectPassword,
  LoginAlreadyUse,
  LoginService,
  UserService
}
import org.http4s.circe.CirceEntityCodec._
import org.http4s.{HttpRoutes, ResponseCookie, SameSite}
import org.http4s.dsl.Http4sDsl

object LoginRoutes {

  def of[F[_]: Async](service: LoginService[F], auth: SecuredRequestHandler[F, User]): AppRoutes[F] = new AppRoutes[F]
    with Http4sDsl[F] {

    override def prefixPath: String = "login"

    val endpoints: HttpRoutes[F] = HttpRoutes.of {
      case req @ POST -> Root =>
        (for {
          authInfo <- req.as[UserAuthInfo]
          token    <- service.login(authInfo)
          res      <- Ok().map(_.addCookie(ResponseCookie("authToken", token, secure = true, sameSite = SameSite.None.some)))
        } yield res).handleErrorWith {
          case IncorrectLogin    => BadRequest(IncorrectLogin.getMessage)
          case IncorrectPassword => BadRequest(IncorrectPassword.getMessage)
        }

      case req @ GET -> Root / "test" => Ok(req.cookies.find(_.name == "authToken").get.content)

    }

    override protected def serviceRoutes: HttpRoutes[F] = endpoints
  }
}
