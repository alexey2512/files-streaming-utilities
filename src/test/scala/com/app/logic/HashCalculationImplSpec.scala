package com.app.logic

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import fs2.io.file._
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import java.nio.charset._

class HashCalculationImplSpec extends AsyncFlatSpec with Matchers with AsyncIOSpec {

  val hc: HashCalculation[IO] = HashCalculationImpl[IO]

  case class Test(
    algorithm: String,
    filename: String,
    content: String,
    encoding: Charset
  )

  val tests: List[Test] = List(
    Test("SHA-256", "a.txt", "1\n2\n3\n   4", StandardCharsets.UTF_8),
    Test("SHA-256", "b.txt", "yandex", StandardCharsets.UTF_16),
    Test("MD5", "c.txt", "don't you wait up for me", StandardCharsets.UTF_8),
    Test("MD5", "d.txt", "i would rather be", StandardCharsets.UTF_16),
    Test("SHA-384", "e.txt", "aba aba \n\n\n aba aba", StandardCharsets.US_ASCII),
    Test("SHA-512", "f.txt", "what is love? baby don't heart me", StandardCharsets.UTF_16BE),
    Test("SHA3-256", "g.txt", "take me to church", StandardCharsets.UTF_16LE)
  )

  tests.foreach(test => {
    s"on ${test.filename}" should "calculate hash correctly" in {
      val digester: Digester = new Digester(test.algorithm)
      digester.createContent(test.filename, test.content, test.encoding)
      val fileHash: String = hc
        .calculateHash(
          Files[IO].readAll(Path(test.filename)),
          test.algorithm
        )
        .unsafeRunSync()
        .getOrElse("")
      val digesterHash: String = digester.getHash(test.filename)
      fileHash shouldEqual digesterHash
      digester.deleteContent(test.filename)
      succeed
    }
  })

}
