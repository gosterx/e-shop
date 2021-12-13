package com.project.eshop.repository

import com.project.eshop.domain.Order
import com.project.eshop.routes.dto.{OrderInfo, ProductInOrderInfo}
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator

object OrderSQL {
  def createOrder(userId: String, amount: Int, address: String, phone: String, payment: String) =
    sql"INSERT INTO orders (user_id, amount, address, phone, payment) VALUES " ++
      sql"(CAST($userId as int), $amount, $address, $phone, $payment)"

  def createOrderedProduct(orderId: Int, product: ProductInOrderInfo) =
    sql"INSERT INTO product_in_order (order_id, id, title, size, color, price, quantity) VALUES " ++
      sql"(CAST($orderId as int), CAST(${product.id} as int), ${product.title}, ${product.size}, ${product.color}, ${product.price}, ${product.quantity})"

  def getOrders =
    sql"SELECT order1.id, user1.first_name, user1.last_name, user1.email, order1.address, order1.phone, order1.payment, order1.amount, order1.status " ++
      sql"FROM orders order1 JOIN users user1 ON order1.user_id = user1.id"
}

trait OrderRepository[F[_]] {
  def createOrder(order: OrderInfo): F[Int]

  def createOrderedProduct(orderId: Int, product: ProductInOrderInfo): F[Int]

  def getOrders: F[List[Order]]
}

object OrderRepository {
  def make: OrderRepository[ConnectionIO] = new OrderRepository[ConnectionIO] {
    override def createOrder(order: OrderInfo): ConnectionIO[Int] =
      OrderSQL.createOrder(order.userId, order.total, order.address, order.phoneNumber, order.paymentMethods).update.withUniqueGeneratedKeys[Int]("id")

    override def createOrderedProduct(orderId: Int, product: ProductInOrderInfo): ConnectionIO[Int] =
      OrderSQL.createOrderedProduct(orderId, product).update.run

    override def getOrders: ConnectionIO[List[Order]] =
      OrderSQL.getOrders.query[Order].to[List]
  }
}
