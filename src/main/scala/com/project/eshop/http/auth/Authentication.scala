package com.project.eshop.http.auth

import cats.Monad
import cats.data.OptionT
import com.project.eshop.domain.User
import com.project.eshop.http.auth.AuthenticationMiddleware.AuthCookie
import com.project.eshop.service.AuthenticationService
import org.http4s.{ContextRequest, Request}

class Authentication [F[_]: Monad](service: AuthenticationService[F]) extends AuthenticationMiddleware [F, User]{
  private def authenticate(raw: String): F[Option[User]] =
    service.getUser(raw)

  override def parseRaw(raw: String, request: Request[F]): OptionT[F, ContextRequest[F, User]] =
    for {
      context <- OptionT(authenticate(raw))
    } yield ContextRequest(context, request)

  override def extractRawOption(request: Request[F]): Option[String] =
    request.cookies.find(_.name == AuthCookie).map(_.content)
}
