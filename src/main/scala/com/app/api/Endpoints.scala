package com.app.api

import com.app.error.ApiError
import com.app.error.ApiError.errorVariants
import fs2.Stream
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir._

class Endpoints[F[_]] {

  val hashSumEndpoint
    : Endpoint[Unit, (Stream[F, Byte], String), ApiError, String, Any with Fs2Streams[F]] =
    endpoint.post
      .in("files" / "hash")
      .in(streamBinaryBody(Fs2Streams[F])(CodecFormat.OctetStream()))
      .in(query[String]("algorithm").default("SHA-256"))
      .out(stringBody)
      .errorOut(errorVariants)

  private type TransformEndpoint =
    Endpoint[Unit, (Stream[F, Byte], String), ApiError, Stream[F, Byte], Any with Fs2Streams[F]]

  private def transformEndpoint(route: String, default: String): TransformEndpoint =
    endpoint.post
      .in("files" / route)
      .in(streamBinaryBody(Fs2Streams[F])(CodecFormat.OctetStream()))
      .in(query[String]("algorithm").default(default))
      .out(streamBinaryBody(Fs2Streams[F])(CodecFormat.OctetStream()))
      .errorOut(errorVariants)

  val compressEndpoint: TransformEndpoint   = transformEndpoint("compress", "gzip")
  val decompressEndpoint: TransformEndpoint = transformEndpoint("decompress", "gunzip")

}

object Endpoints {
  def apply[F[_]]: Endpoints[F] = new Endpoints[F]
}
