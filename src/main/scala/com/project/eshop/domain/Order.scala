package com.project.eshop.domain

case class Order(
  id: String,
  firstName: String,
  lastName: String,
  email: String,
  address: String,
  phone: String,
  payment: String,
  amount: String,
  status: String
)
