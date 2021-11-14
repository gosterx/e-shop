package com.project.eshop.boot

import cats.effect.{ExitCode, IO, IOApp}

object AppRunner extends IOApp{
  override def run(args: List[String]): IO[ExitCode] =
    App.run[IO].use(_ => IO.never).as(ExitCode.Success)
}
