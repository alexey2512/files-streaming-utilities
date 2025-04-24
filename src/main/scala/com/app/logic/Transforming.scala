package com.app.logic

import com.app.error.ApiError
import fs2.Stream

trait Transforming[F[_]] {

  def compressFile(
    stream: Stream[F, Byte],
    algorithm: String
  ): F[Either[ApiError, Stream[F, Byte]]]

  def decompressFile(
    stream: Stream[F, Byte],
    algorithm: String
  ): F[Either[ApiError, Stream[F, Byte]]]

}
