package com.project.eshop.http.auth

import cats.{Applicative, Monad, MonadError}
import cats.data.{Kleisli, OptionT}
import com.project.eshop.domain.{Role, User}
import org.http4s.{ContextRequest, Response, Status}
import com.project.eshop.domain.User._

trait Authorization[F[_], I] {
  def isAuthorized(toAuth: ContextRequest[F, I]): OptionT[F, ContextRequest[F, I]]
}

object AuthorizationMiddleware {
  private def rbacAllRoles[F[_]](implicit me: MonadError[F, Throwable]): RBAC[F, Role, User] =
    RBAC.all[F, Role, User]

  def allRolesHandler[F[_]](
    pf: PartialFunction[ContextRequest[F, User], F[Response[F]]],
  )(
    onNotAuthorized: AuthService[F, User]
  )(implicit me: MonadError[F, Throwable]): AuthService[F, User] =
    AuthorizationMiddleware.withAuthorizationHandler(rbacAllRoles[F])(
      pf,
      onNotAuthorized.run,
    )

  def allRoles[F[_]](
    pf: PartialFunction[ContextRequest[F, User], F[Response[F]]],
  )(implicit me: MonadError[F, Throwable]): AuthService[F, User] =
    allRolesHandler(pf)(defaultUnauthorized)

  private def rbacAdminOnly[F[_]](implicit me: MonadError[F, Throwable]): RBAC[F, Role, User] =
    RBAC[F, Role, User](Role.Admin)

  def adminOnly[F[_]](
    pf: PartialFunction[ContextRequest[F, User], F[Response[F]]],
  )(implicit me: MonadError[F, Throwable]): AuthService[F, User] =
    AuthorizationMiddleware.withAuthorizationHandler(rbacAdminOnly[F])(
      pf,
      defaultUnauthorized.run
    )

  private def defaultUnauthorized[F[_]: Applicative] =
    Kleisli[OptionT[F, *], ContextRequest[F, User], Response[F]](_ =>
      OptionT(Applicative[F].pure(Some(Response[F](Status.Forbidden))))
    )

  def withAuthorizationHandler[F[_]: Monad, I](auth: Authorization[F, I])(
    pf: PartialFunction[ContextRequest[F, I], F[Response[F]]],
    onNotAuthorized: ContextRequest[F, I] => OptionT[F, Response[F]]
  ): AuthService[F, I] =
    Kleisli { req: ContextRequest[F, I] =>
      auth
        .isAuthorized(req)
        .flatMap(_ => pf.andThen(OptionT.liftF(_)).applyOrElse(req, Function.const(OptionT.none[F, Response[F]])))
        .orElse(onNotAuthorized(req))
    }
}
