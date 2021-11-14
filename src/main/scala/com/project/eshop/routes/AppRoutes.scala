package com.project.eshop.routes

import cats.Monad
import com.project.eshop.auth.User
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.server.Router

abstract class AppRoutes[F[_]: Monad] {
  def prefixPath: String

  def routes(): HttpRoutes[F] = Router(
    prefixPath -> notAuthRoutes
  )

  protected def notAuthRoutes: HttpRoutes[F] = HttpRoutes.empty

  protected def authRoutes: AuthedRoutes[User, F] = AuthedRoutes.empty[User, F]
}
