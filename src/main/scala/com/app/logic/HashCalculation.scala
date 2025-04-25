package com.app.logic

import com.app.error.ApiError
import fs2.Stream

trait HashCalculation[F[_]] {

  def calculateHash(
    stream: Stream[F, Byte],
    algorithm: String
  ): F[Either[ApiError, String]]

}
