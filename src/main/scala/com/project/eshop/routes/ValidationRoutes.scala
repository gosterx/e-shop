package com.project.eshop.routes

import cats.effect.kernel.Async
import cats.syntax.all._
import com.project.eshop.domain.User
import com.project.eshop.http.auth.SecuredRequestHandler
import com.project.eshop.routes.dto.PasswordValidation
import com.project.eshop.service.errors.ValidationError
import com.project.eshop.service.{LoginService, ValidationService}
import io.circe.generic.auto.exportEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.{HttpRoutes, ResponseCookie, SameSite}
import org.http4s.dsl.Http4sDsl

object ValidationRoutes {

  def of[F[_]: Async](service: ValidationService[F], auth: SecuredRequestHandler[F, User]): AppRoutes[F] =
    new AppRoutes[F] with Http4sDsl[F] {

      override def prefixPath: String = "validation"

      val endpoints: HttpRoutes[F] = HttpRoutes.of {
        case req @ POST -> Root / "name" =>
          for {
            name   <- req.as[String]
            result <- service.validateName(name)
            res    <- result.fold(Ok(ValidationError("")))(Ok(_))
          } yield res

        case req @ POST -> Root / "lastName" =>
          for {
            lastName <- req.as[String]
            result   <- service.validateLastName(lastName)
            res      <- result.fold(Ok(ValidationError("")))(Ok(_))
          } yield res

        case req @ POST -> Root / "username" =>
          for {
            username <- req.as[String]
            result   <- service.validateUsername(username)
            res      <- result.fold(Ok(ValidationError("")))(Ok(_))
          } yield res

        case req @ POST -> Root / "email" =>
          for {
            email  <- req.as[String]
            result <- service.validateEmail(email)
            res    <- result.fold(Ok(ValidationError("")))(Ok(_))
          } yield res

        case req @ POST -> Root / "password" =>
          for {
            password <- req.as[String]
            result   <- service.validatePassword(password)
            res      <- result.fold(Ok(ValidationError("")))(Ok(_))
          } yield res

        case req @ POST -> Root / "confirmPassword" => {
          import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
          for {
            passwords <- req.as[PasswordValidation]
            result    <- service.validateConfirmPassword(passwords)
            res       <- result.fold(Ok(ValidationError("")))(Ok(_))
          } yield res
        }
      }

      override protected def serviceRoutes: HttpRoutes[F] = endpoints
    }

}
