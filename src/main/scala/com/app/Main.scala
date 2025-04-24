package com.app

import cats.effect._
import com.app.api._
import com.app.config._
import com.app.logic._
import pureconfig.ConfigSource
import pureconfig.module.catseffect.syntax._
import tofu.syntax.logging._
import tofu.logging.Logging
import org.http4s.blaze.server.BlazeServerBuilder

object Main extends IOApp {

  private val IOLoggingMake: Logging.Make[IO] = Logging.Make.plain[IO]

  private def getIORouter: Router[IO] = {
    implicit val apiLogging: Logging[IO] = IOLoggingMake.byName("com.app.api.log")
    val hc: HashCalculation[IO]          = HashCalculationImpl[IO]
    val tf: Transforming[IO]             = TransformingImpl[IO]
    val ep: Endpoints[IO]                = Endpoints[IO]
    val lh: LogHelpers[IO]               = LogHelpers[IO]
    Router(hc, tf, ep, lh)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    implicit val appLogger: Logging[IO] = IOLoggingMake.byName("com.app.app.log")
    for {
      _ <- Resource.make[IO, Unit](
        info"starting application"
      )(_ => info"application closed")
      config <- Resource.eval(ConfigSource.default.loadF[IO, AppConfig]().map(_.server))
      routes <- Resource.eval(getIORouter.httpRoutes)
      appStream = BlazeServerBuilder[IO]
        .bindHttp(config.port, config.host)
        .withHttpApp(routes.orNotFound)
        .serve
      _ <- Resource.make(
        info"application started, listening https://${config.host}:${config.port}"
      )(_ => info"closing application")
      _ <- Resource.eval(appStream.compile.drain)
    } yield ()
  }.use(_ => IO(ExitCode.Success))

}
