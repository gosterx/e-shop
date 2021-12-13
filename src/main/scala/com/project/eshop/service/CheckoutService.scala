package com.project.eshop.service

import cats.effect.kernel.Sync
import cats.syntax.all._
import com.project.eshop.domain.Order
import com.project.eshop.repository.OrderRepository
import com.project.eshop.routes.dto.OrderInfo
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor

trait CheckoutService [F[_]]{
  def checkout(order: OrderInfo): F[Int]

  def getOrders: F[List[Order]]
}

object CheckoutService {
  def of[F[_]: Sync](orderRepository: OrderRepository[ConnectionIO], transactor: Transactor[F]): CheckoutService[F] = new CheckoutService[F] {
    override def checkout(order: OrderInfo): F[Int] =
      (for {
        orderId <- orderRepository.createOrder(order)
        _ <- order.products.map(info => orderRepository.createOrderedProduct(orderId, info)).sequence
      } yield orderId).transact(transactor)

    override def getOrders: F[List[Order]] =
      orderRepository.getOrders.transact(transactor)
  }
}
