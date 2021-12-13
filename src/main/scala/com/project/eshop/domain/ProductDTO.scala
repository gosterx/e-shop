package com.project.eshop.domain

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class ProductDTO(
  title: String,
  description: String,
  image: String,
  categories: List[String],
  size: List[String],
  color: List[String],
  price: Int,
  inStock: Boolean = true
)

object ProductDTO {
  implicit val decoder: Decoder[ProductDTO] = deriveDecoder
}
