package com.project.eshop.routes.dto

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

object UserDTO {
  final case class CreateUser(
    username: String,
    email: String,
    password: String,
    lastName: String,
    firstName: String
  )

  object CreateUser {
    implicit val decoder: Decoder[CreateUser] = deriveDecoder
  }

  final case class UserAuthInfo(username: String, password: String)

  object UserAuthInfo {
    implicit val decoder: Decoder[UserAuthInfo] = deriveDecoder
  }

}
