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

import scala.annotation.StaticAnnotation

import sttp.model.StatusCode

/** This annotation can be used on types used as endpoints error outputs to mark the status code that should used when
  * they are returned in a route.
  *
  * @example
  *   {{{
  *     sealed trait MyError
  *     @code(NotFound) case class UserNotFound(name: String) extends MyError
  *     @code(Forbidden) case class Unauthorized(id: String) extends MyError
  *   }}}
  */
final case class code(code: StatusCode) extends StaticAnnotation
