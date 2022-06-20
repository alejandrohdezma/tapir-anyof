/*
 * Copyright 2022 Alejandro Hernández <https://github.com/alejandrohdezma>
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

package com.alejandrohdezma

import scala.annotation.nowarn
import scala.reflect.ClassTag

import shapeless.Annotation
import sttp.tapir.SchemaType._
import sttp.tapir._

package object tapir {

  @SuppressWarnings(Array("scalafix:DisableSyntax.defaultArgs"))
  implicit class SchemaDiscriminatorOps[A](schema: Schema[A]) {

    /** Adds a discriminator field to a `Schema` whose inner type is a `SCoproduct` schema type. This involves two
      * things:
      *
      *   - Adding the actual discriminator value to the `SCoproduct` itself.
      *   - Adding the discriminator as a field to every subtype of the coproduct.
      *
      * @param discriminatorName
      *   the name of the discriminator field.
      * @param nameToDiscriminator
      *   the method to transform a class simple name into a discriminator.
      * @throws java.lang.RuntimeException
      *   if the schema's inner type is not a `SCoproduct`
      * @return
      *   the `Schema` with its type updated with the discriminator information.
      */
    @nowarn("msg=erasure")
    def addDiscriminator(discriminatorName: String, nameToDiscriminator: String => String = identity): Schema[A] = {
      val schemaType = schema.schemaType match {
        case sCoproduct: SCoproduct[A] =>
          val fieldName = FieldName(discriminatorName)

          val (subtypes, mappings) = sCoproduct.subtypes.flatMap {
            case subSchema @ Schema(st: SProduct[A], Some(name), _, _, _, _, _, _, _, _, _) =>
              val discriminator  = nameToDiscriminator(name.fullName.split("\\.").last)
              val fieldValidator = Validator.enumeration(List(discriminator), Some(_: String))
              val fieldSchema    = Schema.schemaForString.validate(fieldValidator)
              val field          = SProductField[A, String](fieldName, fieldSchema, _ => None)

              List((subSchema.copy(schemaType = st.copy(fields = st.fields :+ field)), (discriminator, SRef(name))))
            case _ => Nil
          }.unzip

          val discriminator = SDiscriminator(fieldName, mappings.toMap)

          SCoproduct(subtypes, Some(discriminator))(sCoproduct.subtypeSchema)
        case _ => sys.error(s"Schema must be of type SCoproduct but ${schema.show}")
      }

      schema.copy(schemaType = schemaType)
    }

  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf", "scalafix:DisableSyntax.=="))
  private[tapir] def classContext[A <: E, E](implicit
      annotation: Annotation[code, A],
      tag: ClassTag[A],
      eSchema: Schema[E]
  ) = eSchema.schemaType match {
    case SCoproduct(subtypes, Some(discriminator)) =>
      val name = tag.runtimeClass.getCanonicalName().replaceAll("\\$", "")

      val discriminatorValue = discriminator.mapping
        .find(_._2.name.fullName == name)
        .map(_._1)
        .getOrElse(sys.error(s"Unable to find discriminator for $name inside ${discriminator.mapping}"))

      val schema = subtypes
        .find(_.name.map(_.fullName).contains(name))
        .map(_.asInstanceOf[Schema[A]])
        .getOrElse(sys.error(s"Unable to find schema for $name in $subtypes"))

      new ClassContext[A](tag, annotation.apply().code, discriminatorValue, schema)
    case SCoproduct(_, None) => sys.error(s"Schema must contain a SDiscriminator")
    case _                   => sys.error(s"Schema must be of type SCoproduct but ${eSchema.show}")
  }

}
