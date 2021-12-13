package com.project.eshop.repository

import com.project.eshop.domain.Category
import com.project.eshop.routes.dto.CategoryDTO
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator

object CategorySQL {
  def create(image: String, title: String) =
    sql"INSERT INTO categories(image, title) VALUES ($image, $title)"

  def get =
    sql"SELECT * FROM categories"
}

trait CategoryRepository[F[_]]{
  def create(category: CategoryDTO): F[Int]

  def get: F[List[Category]]
}

object CategoryRepository {
  def make: CategoryRepository[ConnectionIO] = new CategoryRepository[ConnectionIO] {
    override def create(category: CategoryDTO): ConnectionIO[Int] =
      CategorySQL.create(category.image, category.title).update.run

    override def get: ConnectionIO[List[Category]] =
      CategorySQL.get.query[Category].to[List]
  }
}
