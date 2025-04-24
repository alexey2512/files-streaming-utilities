package com.app.error

import sttp.tapir.EndpointOutput.{OneOf, OneOfVariant}
import sttp.tapir.json.tethysjson.jsonBody
import sttp.model.StatusCode
import sttp.tapir.{Schema, oneOf, oneOfVariant}
import derevo.derive
import tofu.logging.derivation.loggable
import tethys._
import tethys.derivation.semiauto._

@derive(loggable)
sealed trait ApiError {
  val message: String
}

object ApiError {

  @derive(loggable)
  case class NotFoundError(message: String) extends ApiError

  object NotFoundError {

    implicit val schema: Schema[NotFoundError]           = Schema.derived
    implicit val writer: JsonObjectWriter[NotFoundError] = jsonWriter[NotFoundError]
    implicit val reader: JsonReader[NotFoundError]       = jsonReader[NotFoundError]

  }

  @derive(loggable)
  case class InternalServerError(message: String) extends ApiError

  object InternalServerError {

    implicit val schema: Schema[InternalServerError]           = Schema.derived
    implicit val writer: JsonObjectWriter[InternalServerError] = jsonWriter[InternalServerError]
    implicit val reader: JsonReader[InternalServerError]       = jsonReader[InternalServerError]

  }

  private val notFoundErrorV: OneOfVariant[NotFoundError] =
    oneOfVariant(
      StatusCode.NotFound,
      jsonBody[NotFoundError].description("when given algorithm not found")
    )

  private val internalServerErrorV: OneOfVariant[InternalServerError] =
    oneOfVariant(
      StatusCode.InternalServerError,
      jsonBody[InternalServerError].description("when server error occurred")
    )

  val errorVariants: OneOf[ApiError, ApiError] = oneOf[ApiError](
    notFoundErrorV,
    internalServerErrorV
  )

}
