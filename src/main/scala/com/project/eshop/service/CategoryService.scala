package com.project.eshop.service

import cats.effect.kernel.Sync
import com.project.eshop.domain.Category
import com.project.eshop.repository.CategoryRepository
import com.project.eshop.routes.dto.CategoryDTO
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor

trait CategoryService [F[_]]{
  def create(category: CategoryDTO): F[Int]

  def get: F[List[Category]]
}

object CategoryService {
  def of[F[_]: Sync](categoryRepository: CategoryRepository[ConnectionIO], transactor: Transactor[F]): CategoryService[F] =
    new CategoryService[F] {
      override def create(category: CategoryDTO): F[Int] =
        categoryRepository.create(category).transact(transactor)

      override def get: F[List[Category]] =
        categoryRepository.get.transact(transactor)
    }
}
