package com.project.eshop.http

import cats.data.{Kleisli, OptionT}
import org.http4s.{ContextRequest, Request, Response}
import org.http4s.server.Middleware

package object auth {
  type AuthMiddleware[F[_], I] = Middleware[OptionT[F, *], ContextRequest[F, I], Response[F], Request[F], Response[F]]
  type AuthService[F[_], I]    = Kleisli[OptionT[F, *], ContextRequest[F, I], Response[F]]
}
