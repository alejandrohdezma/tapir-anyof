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

import scala.reflect.ClassTag

import sttp.model.StatusCode
import sttp.tapir.Schema
import sttp.tapir.SchemaType.SRef

final private[tapir] class ClassContext[A](
    val tag: ClassTag[A],
    val statusCode: StatusCode,
    val discriminator: String,
    val schema: Schema[A]
) {

  /** Determines if the specified object is a value of `A` using its `ClassTag`. */
  def isInstance(any: Any) = tag.runtimeClass.isInstance(any)

  /** Returns the discrminator mapping for the current class.
    *
    * @example
    *   ```
    *   // For class `org.my.SomeError` with kebab-case discriminator:
    *   "some-error" -> SRef("org.my.SomeError")
    *   ```
    */
  lazy val mapping = discriminator -> SRef[A](schema.name.getOrElse(sys.error(s"$schema must have a name")))

}
