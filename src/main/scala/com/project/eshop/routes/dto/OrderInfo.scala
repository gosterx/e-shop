package com.project.eshop.routes.dto

case class OrderInfo(
  userId: String,
  address: String,
  phoneNumber: String,
  total: Int,
  paymentMethods: String,
  products: List[ProductInOrderInfo]
)

case class ProductInOrderInfo(
  id: String,
  title: String,
  size: String,
  color: String,
  price: Int,
  quantity: Int
)
