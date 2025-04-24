package com.app.logic

import cats.data.EitherT
import cats.effect.Async
import com.app.error.ApiError
import com.app.error.ApiError._
import fs2._
import fs2.compression._

class TransformingImpl[F[_]: Async: Compression] extends Transforming[F] {

  private def getCompressionPipe(algorithm: String): Either[ApiError, Pipe[F, Byte, Byte]] =
    algorithm.toLowerCase match {
      case "deflate" => Right(Compression[F].deflate(DeflateParams.DEFAULT))
      case "gzip"    => Right(Compression[F].gzip(4096))
      case _         => Left(NotFoundError("no such algorithm. try \"deflate\"/\"gzip\""))
    }

  private def getDecompressionPipe(algorithm: String): Either[ApiError, Pipe[F, Byte, Byte]] =
    algorithm.toLowerCase match {
      case "inflate" => Right(Compression[F].inflate(InflateParams.DEFAULT))
      case "gunzip"  => Right(s => Compression[F].gunzip(4096)(s).flatMap(_.content))
      case _         => Left(NotFoundError("no such algorithm. try \"inflate\"/\"gunzip\""))
    }

  private def transform(
    stream: Stream[F, Byte],
    pipe: Either[ApiError, Pipe[F, Byte, Byte]]
  ): F[Either[ApiError, Stream[F, Byte]]] =
    (for {
      pp <- EitherT.fromEither(pipe)
      result = stream.through(pp)
    } yield result).value

  override def compressFile(
    stream: Stream[F, Byte],
    algorithm: String
  ): F[Either[ApiError, Stream[F, Byte]]] =
    transform(stream, getCompressionPipe(algorithm))

  override def decompressFile(
    stream: Stream[F, Byte],
    algorithm: String
  ): F[Either[ApiError, Stream[F, Byte]]] =
    transform(stream, getDecompressionPipe(algorithm))

}

object TransformingImpl {
  def apply[F[_]: Async: Compression]: TransformingImpl[F] = new TransformingImpl[F]
}
