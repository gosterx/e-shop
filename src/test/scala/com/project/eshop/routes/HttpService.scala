package com.project.eshop.routes

import cats.effect.{Resource, Sync}
import cats.effect.kernel.Async
import cats.syntax.all._
import org.http4s.Status.Successful
import org.http4s.{Request, Response}
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import org.http4s.client.middleware.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class HttpService[F[_]: Async](client: Client[F]) {
  def makeRequest(req: Request[F]): F[Response[F]] = {
    for {
      response <- client.run(req).use { res => Sync[F].delay(res)}
    } yield response
  }
}

object HttpService {
  def of[F[_]: Async](
    clientReadTimeout: FiniteDuration,
    executionContext: ExecutionContext
  ): Resource[F, HttpService[F]] = for {
    httpClient <- BlazeClientBuilder[F](executionContext)
      .withRequestTimeout(clientReadTimeout)
      .resource
    loggedClient = Logger[F](logHeaders = true, logBody = true)(httpClient)
    httpService  = HttpService(loggedClient)
  } yield httpService

  def apply[F[_]: Async](httpClient: Client[F]) = new HttpService[F](httpClient)
}
