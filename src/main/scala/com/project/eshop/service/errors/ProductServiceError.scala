package com.project.eshop.service.errors

abstract class ProductServiceError(message: String) extends Throwable(message)

case object NoSuchProduct extends ProductServiceError("No product with such id")
