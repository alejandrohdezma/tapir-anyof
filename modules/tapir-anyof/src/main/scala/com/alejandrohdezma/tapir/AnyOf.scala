/*
 * Copyright 2022-2023 Alejandro Hernández <https://github.com/alejandrohdezma>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alejandrohdezma.tapir

import scala.reflect.ClassTag

import shapeless.Annotation
import sttp.tapir.EndpointIO
import sttp.tapir.EndpointOutput
import sttp.tapir.Mapping
import sttp.tapir.Schema
import sttp.tapir.SchemaType.SCoproduct
import sttp.tapir.SchemaType.SDiscriminator
import sttp.tapir.SchemaType.SchemaWithValue
import sttp.tapir.oneOfVariantClassMatcher
import sttp.tapir.oneOfVariantValueMatcher

/** Allows creating an `EndpointOutput` for a certain error type with multiple errors per status-code.
  *
  * It is required that a `Schema` instance whose type is a `SCoproduct` with all the required information (schemas for
  * child classes and discriminator information) is present.
  *
  * @example
  *   {{{
  *   import com.alejandrohdezma.tapir._
  *   import derevo.derive
  *   import io.circe.generic.extras.Configuration
  *   import io.circe.generic.extras.ConfiguredJsonCodec
  *   import sttp.model.StatusCode._
  *   import sttp.tapir._
  *   import sttp.tapir.derevo.schema
  *   import sttp.tapir.json.circe.jsonBody
  *
  *   @ConfiguredJsonCodec sealed trait MyError
  *   @code(NotFound) @derive(schema) case class UserNotFound(name: String) extends MyError
  *   @code(Forbidden) @derive(schema) case class Unauthorized(id: String) extends MyError
  *
  *   object anyOf extends AnyOf[MyError](jsonBody)
  *
  *   object MyError {
  *
  *     implicit val configuration = Configuration.default.withDiscriminator("error")
  *
  *     implicit lazy val MyErrorSchema: Schema[MyError] = Schema.derived[MyError].addDiscriminator("error")
  *
  *   }
  *
  *   val myEndpoint = endpoint.get
  *     .in("v1" / "users" / path[String]("id"))
  *     .out(stringBody)
  *     .errorOut(anyOf[UserNotFound, Unauthorized])
  *   }}}
  */
class AnyOf[E](endpointIO: EndpointIO.Body[String, E])(implicit schema: Schema[E]) {

  // format: off

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag, E6 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E], classContext[E6, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag, E6 <: E: Annotation[code, *]: ClassTag, E7 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E], classContext[E6, E], classContext[E7, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag, E6 <: E: Annotation[code, *]: ClassTag, E7 <: E: Annotation[code, *]: ClassTag, E8 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E], classContext[E6, E], classContext[E7, E], classContext[E8, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag, E6 <: E: Annotation[code, *]: ClassTag, E7 <: E: Annotation[code, *]: ClassTag, E8 <: E: Annotation[code, *]: ClassTag, E9 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E], classContext[E6, E], classContext[E7, E], classContext[E8, E], classContext[E9, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag, E6 <: E: Annotation[code, *]: ClassTag, E7 <: E: Annotation[code, *]: ClassTag, E8 <: E: Annotation[code, *]: ClassTag, E9 <: E: Annotation[code, *]: ClassTag, E10 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E], classContext[E6, E], classContext[E7, E], classContext[E8, E], classContext[E9, E], classContext[E10, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag, E6 <: E: Annotation[code, *]: ClassTag, E7 <: E: Annotation[code, *]: ClassTag, E8 <: E: Annotation[code, *]: ClassTag, E9 <: E: Annotation[code, *]: ClassTag, E10 <: E: Annotation[code, *]: ClassTag, E11 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E], classContext[E6, E], classContext[E7, E], classContext[E8, E], classContext[E9, E], classContext[E10, E], classContext[E11, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag, E6 <: E: Annotation[code, *]: ClassTag, E7 <: E: Annotation[code, *]: ClassTag, E8 <: E: Annotation[code, *]: ClassTag, E9 <: E: Annotation[code, *]: ClassTag, E10 <: E: Annotation[code, *]: ClassTag, E11 <: E: Annotation[code, *]: ClassTag, E12 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E], classContext[E6, E], classContext[E7, E], classContext[E8, E], classContext[E9, E], classContext[E10, E], classContext[E11, E], classContext[E12, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag, E6 <: E: Annotation[code, *]: ClassTag, E7 <: E: Annotation[code, *]: ClassTag, E8 <: E: Annotation[code, *]: ClassTag, E9 <: E: Annotation[code, *]: ClassTag, E10 <: E: Annotation[code, *]: ClassTag, E11 <: E: Annotation[code, *]: ClassTag, E12 <: E: Annotation[code, *]: ClassTag, E13 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E], classContext[E6, E], classContext[E7, E], classContext[E8, E], classContext[E9, E], classContext[E10, E], classContext[E11, E], classContext[E12, E], classContext[E13, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag, E6 <: E: Annotation[code, *]: ClassTag, E7 <: E: Annotation[code, *]: ClassTag, E8 <: E: Annotation[code, *]: ClassTag, E9 <: E: Annotation[code, *]: ClassTag, E10 <: E: Annotation[code, *]: ClassTag, E11 <: E: Annotation[code, *]: ClassTag, E12 <: E: Annotation[code, *]: ClassTag, E13 <: E: Annotation[code, *]: ClassTag, E14 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E], classContext[E6, E], classContext[E7, E], classContext[E8, E], classContext[E9, E], classContext[E10, E], classContext[E11, E], classContext[E12, E], classContext[E13, E], classContext[E14, E])

  /** Creates an `EndpointOutput` with multiple errors per status-code. 
    * 
    * @throws java.lang.RuntimeException
    *   if the super-type schema is not a `SCoproduct` with a valid discriminator.
    */
  def apply[E1 <: E: Annotation[code, *]: ClassTag, E2 <: E: Annotation[code, *]: ClassTag, E3 <: E: Annotation[code, *]: ClassTag, E4 <: E: Annotation[code, *]: ClassTag, E5 <: E: Annotation[code, *]: ClassTag, E6 <: E: Annotation[code, *]: ClassTag, E7 <: E: Annotation[code, *]: ClassTag, E8 <: E: Annotation[code, *]: ClassTag, E9 <: E: Annotation[code, *]: ClassTag, E10 <: E: Annotation[code, *]: ClassTag, E11 <: E: Annotation[code, *]: ClassTag, E12 <: E: Annotation[code, *]: ClassTag, E13 <: E: Annotation[code, *]: ClassTag, E14 <: E: Annotation[code, *]: ClassTag, E15 <: E: Annotation[code, *]: ClassTag]: EndpointOutput[E] =
    anyOfImpl(classContext[E1, E], classContext[E2, E], classContext[E3, E], classContext[E4, E], classContext[E5, E], classContext[E6, E], classContext[E7, E], classContext[E8, E], classContext[E9, E], classContext[E10, E], classContext[E11, E], classContext[E12, E], classContext[E13, E], classContext[E14, E], classContext[E15, E])

  // format: on

  private val discriminator = schema.schemaType match {
    case SCoproduct(_, Some(discriminator)) => discriminator.name
    case SCoproduct(_, None)                => sys.error("Schema must contain a SDiscriminator")
    case _                                  => sys.error(s"Schema must be of type SCoproduct but ${schema.show}")
  }

  private def anyOfImpl(contexts: ClassContext[_ <: E]*): EndpointOutput[E] = EndpointOutput.OneOf(
    contexts.toList
      .groupBy(_.statusCode)
      .toList
      .sortBy(_._1.code)
      .map {
        case (statusCode, List(context)) =>
          val output = endpointIO
            .copy(codec = endpointIO.codec.schema(context.schema.asInstanceOf[Schema[E]]))
            .description(context.schema.description.getOrElse(""))

          oneOfVariantClassMatcher(statusCode, output, context.tag.runtimeClass)
        case (statusCode, contexts) =>
          val schema = Schema[E](
            SCoproduct(contexts.map(_.schema), Some(SDiscriminator(discriminator, contexts.map(_.mapping).toMap))) {
              e: E => contexts.find(_.isInstance(e)).map(err => SchemaWithValue(err.schema.asInstanceOf[Schema[E]], e))
            }
          )

          oneOfVariantValueMatcher(statusCode, endpointIO.copy(codec = endpointIO.codec.schema(schema))) { case any =>
            contexts.exists(_.isInstance(any))
          }
      },
    Mapping.id
  )

}
