package com.project.eshop.boot.components

import cats.effect.kernel.Sync
import com.project.eshop.repository.UserRepository
import com.project.eshop.service.UserService
import doobie.util.transactor.Transactor

object ServiceModule {
  def make[F[_]: Sync](transactor: Transactor[F]): ServiceModule[F] =
    new ServiceModule[F](UserService.of(UserRepository.make, transactor))
}

final case class ServiceModule[F[_]](userService: UserService[F])
