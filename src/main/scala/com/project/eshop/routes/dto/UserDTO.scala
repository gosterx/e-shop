package com.project.eshop.routes.dto

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

object UserDTO {
  final case class CreateUser(
    login: String,
    email: String,
    password: String,
    lastName: String,
    firstName: String
  )

  object CreateUser {
    implicit val decoder: Decoder[CreateUser] = deriveDecoder
  }

}
