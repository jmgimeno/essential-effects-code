package com.innerproduct.ee.asynchrony

import cats.effect._
import com.innerproduct.ee.debug._
import java.util.concurrent.CompletableFuture
import scala.jdk.FunctionConverters._

object AsyncCompletable extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    effect.debug.as(ExitCode.Success)

  val effect: IO[String] =
    fromCF(IO(cf()))

  def fromCF[A](cfa: IO[CompletableFuture[A]]): IO[A] =
    cfa.flatMap { fa =>
      IO.async_ { cb =>
        val handler: (A, Throwable) => Unit = ??? // <1>

        fa.handle(handler.asJavaBiFunction) // <2>

        ()
      }
    }

  def cf(): CompletableFuture[String] =
    CompletableFuture.supplyAsync(() => "woo!") // <3>
}
