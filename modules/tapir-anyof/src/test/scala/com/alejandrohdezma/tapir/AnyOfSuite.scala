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

import cats.effect.IO

import com.alejandrohdezma.tapir._
import io.circe.Json
import io.circe.syntax._
import munit.Http4sHttpRoutesSuite
import org.http4s.HttpRoutes
import org.http4s.circe._
import sttp.apispec.openapi.circe.yaml._
import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.docs.openapi.OpenAPIDocsOptions
import sttp.tapir.server.http4s.Http4sServerInterpreter

class AnyOfSuite extends Http4sHttpRoutesSuite {

  override val routes: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(
    endpoint.get
      .in("v1" / "users" / path[String]("id"))
      .out(stringBody)
      .errorOut(anyOfThese[UserNotFound, WrongPassword, WrongUser])
      .serverLogic[IO] {
        case "1" => IO(Left(UserNotFound("1")))
        case "2" => IO(Left(WrongPassword("2")))
        case "3" => IO(Left(WrongUser("3")))
        case _   => fail("This should not be called")
      }
  )

  test(GET(uri"/v1/users/1")) { response =>
    assertEquals(response.status.code, 404)
    assertIO(response.as[Json], Json.obj("name" := "1", "error" := "user-not-found"))
  }

  test(GET(uri"/v1/users/2")) { response =>
    assertEquals(response.status.code, 403)
    assertIO(response.as[Json], Json.obj("id" := "2", "error" := "wrong-password"))
  }

  test(GET(uri"/v1/users/3")) { response =>
    assertEquals(response.status.code, 403)
    assertIO(response.as[Json], Json.obj("id" := "3", "error" := "wrong-user"))
  }

  test("anyOf creates endpoint output with discriminator with only one error") {
    val myEndpoint = endpoint.get
      .in("v1" / "users" / path[String]("id"))
      .out(stringBody)
      .errorOut(anyOfThese[UserNotFound])

    val expected =
      """openapi: 3.1.0
        |info:
        |  title: ''
        |  version: latest
        |paths:
        |  /v1/users/{id}:
        |    get:
        |      operationId: getV1UsersId
        |      parameters:
        |      - name: id
        |        in: path
        |        required: true
        |        schema:
        |          type: string
        |      responses:
        |        '200':
        |          description: ''
        |          content:
        |            text/plain:
        |              schema:
        |                type: string
        |        '404':
        |          description: Unable to find user
        |          content:
        |            application/json:
        |              schema:
        |                $ref: '#/components/schemas/UserNotFound'
        |components:
        |  schemas:
        |    UserNotFound:
        |      title: UserNotFound
        |      required:
        |      - name
        |      - error
        |      type: object
        |      properties:
        |        name:
        |          type: string
        |        error:
        |          type: integer
        |          format: int32
        |          enum:
        |          - 1
        |      description: Unable to find user""".stripMargin

    assertNoDiff(toYaml(myEndpoint), expected)
  }

  test("anyOf creates endpoint output with discriminator with multiple errors") {
    val myEndpoint = endpoint.get
      .in("v1" / "users" / path[String]("id"))
      .out(stringBody)
      .errorOut(anyOfThese[UserNotFound, WrongPassword, WrongUser])

    val expected =
      """openapi: 3.1.0
        |info:
        |  title: ''
        |  version: latest
        |paths:
        |  /v1/users/{id}:
        |    get:
        |      operationId: getV1UsersId
        |      parameters:
        |      - name: id
        |        in: path
        |        required: true
        |        schema:
        |          type: string
        |      responses:
        |        '200':
        |          description: ''
        |          content:
        |            text/plain:
        |              schema:
        |                type: string
        |        '403':
        |          description: |-
        |            On 'WrongPassword': Password is invalid
        |            On 'WrongUser': Username is invalid
        |          content:
        |            application/json:
        |              schema:
        |                oneOf:
        |                - $ref: '#/components/schemas/WrongPassword'
        |                - $ref: '#/components/schemas/WrongUser'
        |                discriminator:
        |                  propertyName: error
        |                  mapping:
        |                    '2': '#/components/schemas/WrongPassword'
        |                    '3': '#/components/schemas/WrongUser'
        |        '404':
        |          description: Unable to find user
        |          content:
        |            application/json:
        |              schema:
        |                $ref: '#/components/schemas/UserNotFound'
        |components:
        |  schemas:
        |    UserNotFound:
        |      title: UserNotFound
        |      required:
        |      - name
        |      - error
        |      type: object
        |      properties:
        |        name:
        |          type: string
        |        error:
        |          type: integer
        |          format: int32
        |          enum:
        |          - 1
        |      description: Unable to find user
        |    WrongPassword:
        |      title: WrongPassword
        |      required:
        |      - id
        |      - error
        |      type: object
        |      properties:
        |        id:
        |          type: string
        |        error:
        |          type: integer
        |          format: int32
        |          enum:
        |          - 2
        |      description: Password is invalid
        |    WrongUser:
        |      title: WrongUser
        |      required:
        |      - id
        |      - error
        |      type: object
        |      properties:
        |        id:
        |          type: string
        |        error:
        |          type: integer
        |          format: int32
        |          enum:
        |          - 3
        |      description: Username is invalid""".stripMargin

    assertNoDiff(toYaml(myEndpoint), expected)
  }

  def toYaml(myEndpoint: Endpoint[_, _, _, _, _]): String = {
    val options = OpenAPIDocsOptions.default.copy(defaultDecodeFailureOutput = _ => None)

    val docs = OpenAPIDocsInterpreter(options).toOpenAPI(myEndpoint, "", "latest")

    docs.toYaml
  }

}
