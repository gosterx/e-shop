package com.project.eshop.routes.dto

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class PasswordValidation (password: String, confirm: String)

object PasswordValidation {
  implicit val decoder: Decoder[PasswordValidation] = deriveDecoder
}
