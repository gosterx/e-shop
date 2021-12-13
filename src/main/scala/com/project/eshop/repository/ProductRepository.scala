package com.project.eshop.repository

import com.project.eshop.domain._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.postgres._
import doobie.util.fragment

object ProductSQL {
  val ProductColumns = Seq("id", "title", "description", "image", "categories", "size", "color", "price", "in_stock")

  def create(product: ProductDTO): fragment.Fragment =
    sql"INSERT INTO products (title, description, image, categories, size, color, price)" ++
      sql" VALUES (${product.title}, ${product.description}, ${product.image}, ${product.categories}, ${product.size}, ${product.color}, ${product.price})"

  def selectAll = sql"SELECT * FROM products"

  def selectByCategory(category: String) =
    sql"SELECT * FROM products WHERE $category = ANY (categories)"

  def selectById(id: String) =
    sql"SELECT * FROM products WHERE id=${id.toInt}"
}

trait ProductRepository[F[_]] {
  def create(product: ProductDTO): F[Product]

  def selectAll: F[List[Product]]

  def selectByCategory(category: String): F[List[Product]]

  def selectById(id: String): F[Option[Product]]
}

object ProductRepository {
  def make: ProductRepository[ConnectionIO] = new ProductRepository[ConnectionIO] {
    override def create(product: ProductDTO): ConnectionIO[Product] =
      ProductSQL.create(product).update.withUniqueGeneratedKeys[Product](ProductSQL.ProductColumns: _*)

    override def selectAll: ConnectionIO[List[Product]] = ProductSQL.selectAll.query[Product].to[List]

    override def selectByCategory(category: String): ConnectionIO[List[Product]] =
      ProductSQL.selectByCategory(category).query[Product].to[List]

    override def selectById(id: String): ConnectionIO[Option[Product]] =
      ProductSQL.selectById(id).query[Product].option
  }
}
