package com.app.logic

import fs2.Stream
import com.app.error.ApiError

trait HashCalculation[F[_]] {

  def calculateHash(
    stream: Stream[F, Byte],
    algorithm: String
  ): F[Either[ApiError, String]]

}
