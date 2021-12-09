package com.innerproduct.ee.contexts

import cats.effect._
import com.innerproduct.ee.debug._

object Blocking extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    withBlocker.as(ExitCode.Success) // <1>

  val withBlocker: IO[Unit] =
    for {
      _ <- IO("on default").debug
      _ <- IO.blocking(IO("on blocker").debug)  // <2>
      _ <- IO("where am I?").debug // <3>
    } yield ()
}
