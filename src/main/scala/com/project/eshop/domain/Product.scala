package com.project.eshop.domain

case class Product(
  id: String,
  title: String,
  description: String,
  image: String,
  categories: List[String],
  size: List[String],
  color: List[String],
  price: Int,
  inStock: Boolean = true
)
