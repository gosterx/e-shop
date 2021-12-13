package com.project.eshop.service

import cats.effect.Sync
import cats.implicits.catsSyntaxApplicativeId
import com.project.eshop.domain._
import com.project.eshop.repository.{ProductRepository, TokenRepository, UserRepository}
import com.project.eshop.service.errors.NoSuchProduct
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor


trait ProductService [F[_]]{
  def create(product: ProductDTO): F[Product]

  def getAllProducts(): F[List[Product]]

  def getProductsWithCategory(category: String): F[List[Product]]

  def getProductById(id: String): F[Product]
}

object ProductService {
  def of[F[_]: Sync](productRepository: ProductRepository[ConnectionIO], transactor: Transactor[F]): ProductService[F] =
    new ProductService[F] {
      override def create(product: ProductDTO): F[Product] =
        productRepository.create(product).transact(transactor)

      override def getAllProducts(): F[List[Product]] =
        productRepository.selectAll.transact(transactor)

      override def getProductsWithCategory(category: String): F[List[Product]] =
        productRepository.selectByCategory(category).transact(transactor)

      override def getProductById(id: String): F[Product] =
        (for {
          productOpt <- productRepository.selectById(id)
          product <- productOpt match {
            case Some(value) => Sync[ConnectionIO].pure(value)
            case None => Sync[ConnectionIO].raiseError(NoSuchProduct)
          }
        } yield productOpt.get).transact(transactor)
    }
}
