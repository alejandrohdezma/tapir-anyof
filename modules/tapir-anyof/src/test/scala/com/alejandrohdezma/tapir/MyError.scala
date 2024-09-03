/*
 * Copyright 2022-2024 Alejandro Hernández <https://github.com/alejandrohdezma>
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
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import io.circe.syntax._
import sttp.model.StatusCode._
import sttp.tapir.Schema.annotations.description
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody

sealed trait MyError extends Throwable

@code(NotFound)
@description("Unable to find user")
final case class UserNotFound(name: String) extends MyError

@code(Forbidden)
@description("Password is invalid")
final case class WrongPassword(id: String) extends MyError

@code(Forbidden)
@description("Username is invalid")
final case class WrongUser(id: String) extends MyError

object anyOfThese extends AnyOf[MyError](jsonBody)

object MyError {

  implicit val MyErrorEncoder: Encoder[MyError] = Encoder.instance {
    case UserNotFound(name) => Json.obj("name" := name, "error" := "user-not-found")
    case WrongPassword(id)  => Json.obj("id" := id, "error" := "wrong-password")
    case WrongUser(id)      => Json.obj("id" := id, "error" := "wrong-user")
  }

  implicit val MyErrorDecoder: Decoder[MyError] = Decoder.instance { cursor =>
    cursor.downField("error").as[String].flatMap {
      case "user-not-found" => Decoder.forProduct1("name")(UserNotFound.apply).apply(cursor)
      case "wrong-password" => Decoder.forProduct1("id")(WrongPassword.apply).apply(cursor)
      case "wrong-user"     => Decoder.forProduct1("id")(WrongUser.apply).apply(cursor)
    }
  }

  val mapping: String => Int = {
    case "UserNotFound"  => 1
    case "WrongPassword" => 2
    case "WrongUser"     => 3
  }

  implicit lazy val MyErrorSchema: Schema[MyError] =
    Schema.derived[MyError].addDiscriminatorAs[Int]("error", mapping)

}
