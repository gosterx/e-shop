package com.project.eshop.http.auth

import cats.MonadError
import cats.data.OptionT
import cats.syntax.all._
import org.http4s.ContextRequest
import tsec.authorization.{AuthGroup, AuthorizationInfo, SimpleAuthEnum}

import scala.reflect.ClassTag

sealed abstract case class RBAC[F[_], R, U](authorized: AuthGroup[R])(
  implicit role: AuthorizationInfo[F, R, U],
  enum: SimpleAuthEnum[R, String],
  me:  MonadError[F, Throwable]
) extends Authorization[F, U] {

  def isAuthorized(
    toAuth: ContextRequest[F, U]
  ): OptionT[F, ContextRequest[F, U]] =
    OptionT {
      role.fetchInfo(toAuth.context).map { extractedRole =>
        if (enum.contains(extractedRole) && authorized.contains(extractedRole))
          Some(toAuth)
        else
          None
      }
    }
}

object RBAC {
  def apply[F[_], R: ClassTag, U](roles: R*)(
    implicit enum: SimpleAuthEnum[R, String],
    role: AuthorizationInfo[F, R, U],
    F: MonadError[F, Throwable]
  ): RBAC[F, R, U] =
    fromGroup[F, R, U](AuthGroup(roles: _*))

  def fromGroup[F[_], R: ClassTag, U](valueSet: AuthGroup[R])(
    implicit role: AuthorizationInfo[F, R, U],
    enum: SimpleAuthEnum[R, String],
    F: MonadError[F, Throwable]
  ): RBAC[F, R, U] = new RBAC[F, R, U](valueSet) {}

  def all[F[_], R: ClassTag, U](
    implicit enum: SimpleAuthEnum[R, String],
    role: AuthorizationInfo[F, R, U],
    F: MonadError[F, Throwable]
  ): RBAC[F, R, U] =
    new RBAC[F, R, U](enum.viewAll) {}
}
