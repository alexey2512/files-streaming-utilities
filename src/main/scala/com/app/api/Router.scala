package com.app.api

import com.app.config.AppConfig
import com.app.logic.{HashCalculation, Transforming}
import cats.effect.Async
import cats.syntax.functor._
import org.http4s.HttpRoutes
import pureconfig._
import pureconfig.module.catseffect.syntax._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

class Router[F[_]: Async](
  hc: HashCalculation[F],
  tf: Transforming[F],
  ep: Endpoints[F],
  lh: LogHelpers[F]
) {

  private val swaggerEndpoints: F[List[ServerEndpoint[Any, F]]] =
    ConfigSource.default.loadF[F, AppConfig]().map { conf =>
      SwaggerInterpreter().fromEndpoints(
        List(
          ep.hashSumEndpoint,
          ep.compressEndpoint,
          ep.decompressEndpoint
        ),
        conf.name,
        conf.version
      )
    }

  private val serverEndpoints = List(
    ep.hashSumEndpoint.serverLogic(
      lh.loggingErrors((hc.calculateHash _).tupled, "files/hash")
    ),
    ep.compressEndpoint.serverLogic(
      lh.loggingErrors((tf.compressFile _).tupled, "files/compress")
    ),
    ep.decompressEndpoint.serverLogic(
      lh.loggingErrors((tf.decompressFile _).tupled, "files/decompress")
    )
  )

  val httpRoutes: F[HttpRoutes[F]] = for {
    swg <- swaggerEndpoints
    routes = Http4sServerInterpreter[F]().toRoutes(
      serverEndpoints ++ swg
    )
  } yield routes

}

object Router {

  def apply[F[_]: Async](
    hc: HashCalculation[F],
    tf: Transforming[F],
    ep: Endpoints[F],
    lh: LogHelpers[F]
  ): Router[F] = new Router[F](hc, tf, ep, lh)

}
