package com.project.eshop.http.auth

import cats.{Applicative, Monad}
import cats.data.{Kleisli, OptionT}
import org.http4s.server.AuthMiddleware
import org.http4s.{ContextRequest, Request, Response, Status}

abstract class AuthenticationMiddleware[F[_]: Applicative, I] {

  def parseRaw(raw: String, request: Request[F]): OptionT[F, ContextRequest[F, I]]

  def extractRawOption(request: Request[F]): Option[String]

  def extractAndValidate(
    request: Request[F],
  ): OptionT[F, ContextRequest[F, I]] =
    extractRawOption(request) match {
      case Some(raw) =>
        parseRaw(raw, request)
      case None =>
        OptionT.none[F, ContextRequest[F, I]]
    }
}

object AuthenticationMiddleware {
  private[auth] val AuthCookie = "authToken"

  def apply[F[_]: Monad, I](
    authedStuff: Kleisli[OptionT[F, *], Request[F], ContextRequest[F, I]],
    onNotAuthenticated: Request[F] => F[Response[F]]
  ): AuthMiddleware[F, I] =
    service => {
      Kleisli { r: Request[F] =>
        OptionT.liftF(
          authedStuff
            .run(r)
            .flatMap(service.mapF(o => OptionT.liftF(o.getOrElse(Response[F](Status.NotFound)))).run)
            .getOrElseF(onNotAuthenticated(r))
        )
      }
    }
}
