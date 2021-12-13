package com.project.eshop.http.auth

import cats.MonadError
import cats.data.{Kleisli, OptionT}
import org.http4s.{ContextRequest, HttpRoutes, Request, Response, Status}

class SecuredRequestHandler[F[_], I](val authenticationMiddleware: AuthenticationMiddleware[F, I])(
  implicit F: MonadError[F, Throwable],
  ME: MonadError[Kleisli[OptionT[F, *], Request[F], *], Throwable]
) {
  private[this] val cachedUnauthorized: Response[F]                       = Response[F](Status.Unauthorized)
  private[this] val defaultNotAuthenticated: Request[F] => F[Response[F]] = _ => F.pure(cachedUnauthorized)

  def liftService(
    service: Kleisli[OptionT[F, *], ContextRequest[F, I], Response[F]],
    onNotAuthenticated: Request[F] => F[Response[F]] = defaultNotAuthenticated
  ): HttpRoutes[F] = {
    val middleware = AuthenticationMiddleware(Kleisli(authenticationMiddleware.extractAndValidate), onNotAuthenticated)

    ME.handleErrorWith(middleware(service)) { e: Throwable =>
      println(e.getMessage)
      Kleisli.liftF(OptionT.pure(cachedUnauthorized))
    }
  }

}

object SecuredRequestHandler {
  def apply[F[_], I](authenticationMiddleware: AuthenticationMiddleware[F, I])(
    implicit F: MonadError[F, Throwable]
  ): SecuredRequestHandler[F, I] = new SecuredRequestHandler[F, I](authenticationMiddleware)
}
