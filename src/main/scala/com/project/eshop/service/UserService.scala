package com.project.eshop.service

import cats.effect.Sync
import com.project.eshop.auth.User
import com.project.eshop.repository.UserRepository
import com.project.eshop.routes.dto.UserDTO.CreateUser
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor

trait UserService[F[_]] {
  def create(user: CreateUser): F[User]
}

object UserService {
  def of[F[_]: Sync](userRepository: UserRepository[ConnectionIO], transactor: Transactor[F]): UserService[F] =
    new UserService[F] {
      override def create(user: CreateUser): F[User] =
        (for {
          logins <- userRepository.selectLogins
          _ <-
            if (logins.contains(user.login)) Sync[ConnectionIO].raiseError(LoginAlreadyUse)
            else Sync[ConnectionIO].pure()
          emails <- userRepository.selectEmails
          _ <-
            if (emails.contains(user.email)) Sync[ConnectionIO].raiseError(EmailAlreadyUse)
            else Sync[ConnectionIO].pure()
          user <- userRepository.create(user)
        } yield user).transact(transactor)
    }
}