package com.app.logic

import cats.data.EitherT
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import fs2.Stream
import org.scalatest.Assertion
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class TransformingImplSpec extends AsyncFlatSpec with Matchers with AsyncIOSpec {

  val tf: Transforming[IO] = TransformingImpl[IO]

  def consistentPerformance(in: String, de: String): Assertion = {
    val rand: Random     = new Random()
    val data: List[Byte] = (1 to 100000).map(_ => rand.nextInt().toByte).toList
    val result: List[Byte] = (for {
      compressed   <- EitherT(tf.compressFile(Stream.emits(data).covary[IO], in))
      decompressed <- EitherT(tf.decompressFile(compressed, de))
    } yield decompressed).value
      .unsafeRunSync()
      .getOrElse(Stream.empty.covary[IO])
      .compile
      .toList
      .unsafeRunSync()
    result shouldEqual data
  }

  "gzip-gunzip" should "transform data consistently" in {
    consistentPerformance("gzip", "gunzip")
  }

  "deflate-inflate" should "transform data consistently" in {
    consistentPerformance("deflate", "inflate")
  }

}
