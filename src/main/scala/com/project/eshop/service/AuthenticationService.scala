package com.project.eshop.service

import cats.effect.Sync
import com.project.eshop.domain.User
import com.project.eshop.repository.TokenRepository
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor

trait AuthenticationService [F[_]] {
  def getUser(token: String): F[Option[User]]
}

object AuthenticationService {
  def of[F[_]: Sync](tokenRepository: TokenRepository[ConnectionIO], transactor: Transactor[F]): AuthenticationService[F] =
    new AuthenticationService[F] {
      override def getUser(token: String): F[Option[User]] =
        (for {
          userOpt <- tokenRepository.getUserByToken(token)
        } yield userOpt).transact(transactor)
    }
}


