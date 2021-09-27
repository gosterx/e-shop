package com.project.eshop.codecs

import cats.effect.kernel.Async
import com.project.eshop.appuser.User
import doobie.Read
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

object UserCodecs {

  implicit val userEncoder: Encoder[User] = (user: User) =>
    Json.obj(
      "id" -> user.id.asJson,
      "name"  -> user.name.asJson,
      "email" -> user.email.asJson
    )

  implicit val userDecoder: Decoder[User] = (c: HCursor) =>
    for {
      id <- c.downField("id").as[String]
      name  <- c.downField("name").as[String]
      email <- c.downField("email").as[String]
    } yield User(id, name, email)

  implicit def userEntityDecoder[F[_]: Async]: EntityDecoder[F, User] = jsonOf[F, User]
  implicit def userEntityEncoder[F[_]: Async]: EntityEncoder[F, User] = jsonEncoderOf[F, User]

  implicit val userRead: Read[User] = Read[(String, String, String)].map{
    case (id, name, email) => User(id, name, email)
  }

}
