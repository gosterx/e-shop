package com.project.eshop

import com.project.eshop.domain.User
import org.http4s.{ContextRequest, Response}

package object routes {
  type AuthEndpoint[F[_]] = PartialFunction[ContextRequest[F, User], F[Response[F]]]
}
