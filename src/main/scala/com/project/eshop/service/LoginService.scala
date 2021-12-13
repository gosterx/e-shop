package com.project.eshop.service

import cats.effect.Sync
import com.project.eshop.domain.User
import com.project.eshop.repository.{TokenRepository, UserRepository}
import com.project.eshop.routes.dto.UserDTO.{CreateUser, UserAuthInfo}
import com.project.eshop.service.errors.{IncorrectLogin, IncorrectPassword}
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor

import java.util.{Base64, UUID}

trait LoginService [F[_]]{
  def login(authInfo: UserAuthInfo): F[(String, User)]
}

object LoginService {
  def of[F[_]: Sync](userRepository: UserRepository[ConnectionIO], tokenRepository: TokenRepository[ConnectionIO], transactor: Transactor[F]): LoginService[F] =
    new LoginService[F] {
      override def login(authInfo: UserAuthInfo): F[(String, User)] =
        (for {
          userOpt <- userRepository.selectUserAuthInfoByUsername(authInfo.username)
          user <- userOpt match {
            case Some(value) => Sync[ConnectionIO].pure(value)
            case None => Sync[ConnectionIO].raiseError(IncorrectLogin)
          }
          userInfoOpt <- if (Base64.getEncoder.encodeToString(authInfo.password.getBytes) == user.password) userRepository.selectUserByUsername(authInfo.username)
          else Sync[ConnectionIO].raiseError(IncorrectPassword)
          token <- Sync[ConnectionIO].delay(UUID.randomUUID().toString)
          _ <- tokenRepository.upsert(user.id, token)
        } yield (token, userInfoOpt.get)).transact(transactor)
    }

}