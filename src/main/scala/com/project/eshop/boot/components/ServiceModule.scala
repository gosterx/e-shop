package com.project.eshop.boot.components

import cats.effect.kernel.Sync
import com.project.eshop.repository.{TokenRepository, UserRepository}
import com.project.eshop.service.{AuthenticationService, LoginService, UserService}
import doobie.util.transactor.Transactor

object ServiceModule {
  def make[F[_]: Sync](transactor: Transactor[F]): ServiceModule[F] =
    new ServiceModule[F](
      UserService.of(UserRepository.make, transactor),
      LoginService.of(UserRepository.make, TokenRepository.make, transactor),
      AuthenticationService.of(TokenRepository.make, transactor)
    )
}

final case class ServiceModule[F[_]](
  userService: UserService[F],
  loginService: LoginService[F],
  authenticateService: AuthenticationService[F]
)
