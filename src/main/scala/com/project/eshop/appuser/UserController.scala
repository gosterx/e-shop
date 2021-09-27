package com.project.eshop.appuser

import cats.effect.kernel.Async
import org.http4s.HttpRoutes
import cats.syntax.all._
import com.project.eshop.codecs.UserCodecs._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl

class UserController[F[_]: Async](userRepo: UserRepo[F]) extends Http4sDsl[F] {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "api" / "users" =>
      userRepo.getUsers.flatMap(users => Ok(users))

    case req @ POST -> Root / "api" / "create" / "user" =>
      req.as[User].map(userRepo.addUser).flatMap(Ok(_))
  }

}
