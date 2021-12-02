package com.project.eshop.routes

import cats.Monad
import com.project.eshop.domain.User
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.server.Router

abstract class AppRoutes[F[_]: Monad] {
  def prefixPath: String

  def routes(): HttpRoutes[F] = Router(
    prefixPath -> serviceRoutes
  )

  protected def serviceRoutes: HttpRoutes[F]
}
