package com.app.logic

import cats.effect.Async
import cats.data.EitherT
import cats.syntax.all._
import com.app.error.ApiError
import com.app.error.ApiError._
import fs2.Stream

import java.security.{MessageDigest, NoSuchAlgorithmException}

class HashCalculationImpl[F[_]: Async] extends HashCalculation[F] {

  private def calculateWithDigest(digest: MessageDigest, stream: Stream[F, Byte]): F[String] =
    stream
      .chunkN(4096)
      .fold(digest) { (digest, chunk) =>
        digest.update(chunk.toArray)
        digest
      }
      .map(digest => digest.digest().map("%02x".format(_)).mkString)
      .compile
      .lastOrError

  override def calculateHash(
    stream: Stream[F, Byte],
    algorithm: String
  ): F[Either[ApiError, String]] =
    (for {
      digest <- EitherT.fromEither[F](
        Either.catchNonFatal(MessageDigest.getInstance(algorithm)).left.map {
          case _: NoSuchAlgorithmException => NotFoundError(s"no such algorithm: $algorithm")
          case e => InternalServerError(s"some error occurred: ${e.getMessage}")
        }
      )
      hash <- EitherT(calculateWithDigest(digest, stream).attempt)
        .leftMap(e => InternalServerError(s"hash calculation failed: ${e.getMessage}"): ApiError)
    } yield hash).value

}

object HashCalculationImpl {
  def apply[F[_]: Async]: HashCalculationImpl[F] = new HashCalculationImpl[F]
}
