package com.app.api

import cats.syntax.all._
import cats.Monad
import tofu.logging._

class LogHelpers[F[_]: Logging: Monad] {

  def loggingErrors[I, E: Loggable, O](
    f: I => F[Either[E, O]],
    route: String
  ): I => F[Either[E, O]] = input =>
    for {
      either <- f(input)
      _ <- either match {
        case Left(error) =>
          Logging[F].warn(s"$route: error out", Map("error" -> error))
        case Right(_) => ().pure
      }
    } yield either

}

object LogHelpers {
  def apply[F[_]: Logging: Monad]: LogHelpers[F] = new LogHelpers[F]
}
