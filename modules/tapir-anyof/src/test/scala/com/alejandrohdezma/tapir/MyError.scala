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

import com.alejandrohdezma.tapir._
import derevo.derive
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.ConfiguredJsonCodec
import sttp.model.StatusCode._
import sttp.tapir._
import sttp.tapir.derevo.schema
import sttp.tapir.json.circe.jsonBody

@ConfiguredJsonCodec sealed trait MyError extends Throwable

@code(NotFound)
@derive(schema)
final case class UserNotFound(name: String) extends MyError

@code(Forbidden)
@derive(schema)
final case class WrongPassword(id: String) extends MyError

@code(Forbidden)
@derive(schema)
final case class WrongUser(id: String) extends MyError

object anyOfThese extends AnyOf[MyError](jsonBody)

object MyError {

  implicit val configuration: Configuration =
    Configuration.default.withDiscriminator("error").withKebabCaseConstructorNames

  val mapping: String => Int = {
    case "UserNotFound"  => 1
    case "WrongPassword" => 2
    case "WrongUser"     => 3
  }

  implicit lazy val MyErrorSchema: Schema[MyError] =
    Schema.derived[MyError].addDiscriminatorAs[Int]("error", mapping)

}
