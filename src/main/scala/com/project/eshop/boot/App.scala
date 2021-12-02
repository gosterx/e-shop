package com.project.eshop.boot

import cats.effect.{Async, Resource, Sync}
import com.project.eshop.boot.components.{HttpApi, RepositoryModule, ServiceModule}
import com.project.eshop.http.auth.{Authentication, SecuredRequestHandler}
import org.http4s.server.Server
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object App {

  private def server[F[_] : Async](port: Int, host: String)(executionContext: ExecutionContext)(httpApp: HttpApp[F]): Resource[F, Server] =
    BlazeServerBuilder(executionContext).bindHttp(port, host).withHttpApp(httpApp).resource

  def run[F[_]: Async]: Resource[F, Server] =
    for {
      transactor <- RepositoryModule.make()
      serverPool <- Resource
        .make(Sync[F].delay(Executors.newCachedThreadPool()))(exec => Sync[F].delay(exec.shutdown()))
        .map(ExecutionContext.fromExecutorService)
      services = ServiceModule.make(transactor)
      authenticator = new Authentication(services.authenticateService)
      routeAuth = SecuredRequestHandler(authenticator)
      httpApp = HttpApi.of(routeAuth, services)
      server <- server(8080, "localhost")(serverPool)(httpApp)
    } yield server

}
