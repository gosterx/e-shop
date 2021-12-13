package com.project.eshop.boot.components

import cats.effect.kernel.Sync
import com.project.eshop.repository.{CategoryRepository, OrderRepository, ProductRepository, TokenRepository, UserRepository}
import com.project.eshop.service.{AuthenticationService, CategoryService, CheckoutService, LoginService, ProductService, UserService, ValidationService}
import doobie.util.transactor.Transactor

object ServiceModule {
  def make[F[_]: Sync](transactor: Transactor[F]): ServiceModule[F] =
    new ServiceModule[F](
      UserService.of(UserRepository.make, transactor),
      LoginService.of(UserRepository.make, TokenRepository.make, transactor),
      AuthenticationService.of(TokenRepository.make, transactor),
      ProductService.of(ProductRepository.make, transactor),
      ValidationService.of(UserRepository.make, transactor),
      CheckoutService.of(OrderRepository.make, transactor),
      CategoryService.of(CategoryRepository.make, transactor)
    )
}

final case class ServiceModule[F[_]](
  userService: UserService[F],
  loginService: LoginService[F],
  authenticateService: AuthenticationService[F],
  productService: ProductService[F],
  validationService: ValidationService[F],
  checkoutService: CheckoutService[F],
  categoryService: CategoryService[F]
)
