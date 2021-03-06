package com.project.eshop.codecs

import cats.effect.kernel.Async
import com.project.eshop.domain.User
import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import org.http4s.circe.jsonEncoderOf
import org.http4s.EntityEncoder

object Codecs {

  implicit val userEncoder: Encoder[User] = (user: User) =>
    Json.obj(
      "id" -> user.id.asJson,
      "username" -> user.username.asJson,
      "email" -> user.email.asJson,
      "role" -> user.role.asJson,
      "firstName" -> user.firstName.asJson,
      "lastName" -> user.lastName.asJson
    )

  implicit def userEntityEncoder[F[_]: Async]: EntityEncoder[F, User] = jsonEncoderOf[F, User]

}
