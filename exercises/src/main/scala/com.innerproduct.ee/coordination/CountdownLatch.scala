package com.innerproduct.ee.coordination

import cats.effect._
//import cats.effect.concurrent._
import cats.implicits._

trait CountdownLatch {
  def await: IO[Unit]
  def decrement: IO[Unit]
}

object CountdownLatch {
  def apply(n: Long)(implicit cs: Spawn[IO]): IO[CountdownLatch] =
    for {
      whenDone <- Deferred[IO, Unit]
      state <- Ref[IO].of[State](Outstanding(n, whenDone))
    } yield new CountdownLatch {
      def await: IO[Unit] =
        state.get.flatMap {
          case Outstanding(_, whenDone) => whenDone.get
          case Done()                   => IO.unit
        }

      def decrement: IO[Unit] =
        state.modify {
          case Outstanding(1, whenDone) => Done() -> whenDone.complete(())
          case Outstanding(n, whenDone) =>
            Outstanding(n - 1, whenDone) -> IO.unit
          case Done() => Done() -> IO.unit
        }.flatten.void
    }

  sealed trait State
  case class Outstanding(n: Long, whenDone: Deferred[IO, Unit]) extends State
  case class Done() extends State
}

object LatchExample extends IOApp {
  import com.innerproduct.ee.debug._

  def run(args: List[String]): IO[ExitCode] =
    for {
      latch <- CountdownLatch(1)
      _ <- (actionWithPrerequisites(latch), runPrerequisite(latch)).parTupled
    } yield ExitCode.Success

  def runPrerequisite(latch: CountdownLatch) =
    for {
      result <- IO("prerequisite").debug
      _ <- latch.decrement // <1>
    } yield result

  def actionWithPrerequisites(latch: CountdownLatch) =
    for {
      _ <- IO("waiting for prerequisites").debug
      _ <- latch.await // <1>
      result <- IO("action").debug // <2>
    } yield result
}
