# Tapir extension to simplify creating error outputs for sealed traits

Extension library for [Tapir](https://github.com/softwaremill/tapir) that allows creating an `EndpointOutput` for a certain error type with multiple errors per status-code.

```scala
endpoint.get
  .in("v1" / "users" / path[String]("id"))
  .out(stringBody)
  .errorOut(anyOf[UserNotFound, WrongPassword, WrongUser])
```

<details><summary>See generated OpenApi</summary>

```yaml
paths:
  /v1/users/{id}:
    get:
      operationId: getV1UsersId
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          content:
            text/plain:
              schema:
                type: string
        '403':
          content:
            application/json:
              schema:
                oneOf:
                - $ref: '#/components/schemas/WrongPassword'
                - $ref: '#/components/schemas/WrongUser'
                discriminator:
                  propertyName: error
                  mapping:
                    wrong-password: '#/components/schemas/WrongPassword'
                    wrong-user: '#/components/schemas/WrongUser'
        '404':
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserNotFound'
components:
  schemas:
    UserNotFound:
      required:
      - name
      - error
      type: object
      properties:
        name:
          type: string
        error:
          type: string
          enum:
          - user-not-found
    WrongPassword:
      required:
      - id
      - error
      type: object
      properties:
        id:
          type: string
        error:
          type: string
          enum:
          - wrong-password
    WrongUser:
      required:
      - id
      - error
      type: object
      properties:
        id:
          type: string
        error:
          type: string
          enum:
          - wrong-user
```

</details>

## Installation

Add the following line to your `build.sbt` file:

```sbt
libraryDependencies += "com.alejandrohdezma" %% "tapir-anyof" % "0.5.0")
```

## Usage

In order to use this library you need to follow these simple steps:

### Create your error AST

First create your error as you would normally do using Tapir:

```scala
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec sealed trait MyError
case class UserNotFound(name: String) extends MyError
case class WrongUser(id: String) extends MyError
case class WrongPassword(id: String) extends MyError

object MyError {

  implicit val config: Configuration =
    Configuration.default.withDiscriminator("error")

}
```

> In this case we are using circe's Json, but you could use any available output type.

### Provide `Schema` instances

Then you need to ensure that every error type has a `Schema` instance and that it is annotated with `@code` indicating the status code that should be used when that error is returned:

```scala
import com.alejandrohdezma.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.Schema
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.ConfiguredJsonCodec
import sttp.model.StatusCode._

@ConfiguredJsonCodec sealed trait MyError
@code(NotFound) final case class UserNotFound(name: String) extends MyError
@code(Forbidden) final case class WrongUser(id: String) extends MyError
@code(Forbidden) final case class WrongPassword(id: String) extends MyError

object MyError {

  implicit val config: Configuration =
    Configuration.default.withDiscriminator("error")

  implicit lazy val MyErrorSchema: Schema[MyError] = Schema.derived[MyError]

}
```

### Add discriminator information to `Schema`

Populate the `sealed trait` schema with the discriminator information by calling `addDiscriminator` indicating the discriminator name. This will involve two things: first, adding the actual discriminator value to the `SCoproduct` itself and second, adding the discriminator as a field to every subtype of the coproduct.

```scala
import com.alejandrohdezma.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.Schema
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.ConfiguredJsonCodec
import sttp.model.StatusCode._

@ConfiguredJsonCodec sealed trait MyError
@code(NotFound) final case class UserNotFound(name: String) extends MyError
@code(Forbidden) final case class WrongUser(id: String) extends MyError
@code(Forbidden) final case class WrongPassword(id: String) extends MyError

object MyError {

  implicit val config: Configuration =
    Configuration.default.withDiscriminator("error")

  implicit lazy val MyErrorSchema: Schema[MyError] = Schema.derived[MyError].addDiscriminator("error")

}
```

> Note: if using a different discriminator than the class simple-name (for example, kebab-case) you can add a second parameter to `addDiscriminator` that lets you modify the class simple-name:
>
> ```scala mdoc:silent
> implicit val config: Configuration =
>   Configuration.default.withDiscriminator("error").withKebabCaseConstructorNames
>
> implicit lazy val MyErrorSchema: Schema[MyError] =
>   Schema.derived[MyError].addDiscriminator("error", config.transformConstructorNames)
> ```

**Important:** this method will fail with a runtime exception if used on a schema whose inner type is not a `SCoproduct`:

```scala
import com.alejandrohdezma.tapir._
import sttp.tapir.Schema

final case class SimpleError(name: String)

implicit val SimpleErrorSchema: Schema[SimpleError] = Schema.derived[SimpleError].addDiscriminator("error")
// java.lang.RuntimeException: Schema must be of type SCoproduct but schema is SProduct(List(SProductField(FieldName(name,name),Schema(SString(),None,false,None,None,None,None,false,false,All(List()),AttributeMap(Map())))))
// 	at scala.sys.package$.error(package.scala:27)
// 	at com.alejandrohdezma.tapir.package$SchemaDiscriminatorOps.addDiscriminator(package.scala:66)
// 	at repl.MdocSession$MdocApp3$$anonfun$49.apply$mcV$sp(README.md:139)
// 	at repl.MdocSession$MdocApp3$$anonfun$49.apply(README.md:135)
// 	at repl.MdocSession$MdocApp3$$anonfun$49.apply(README.md:135)
```

### Create your `anyOf` utility


Anywhere in your code create an `anyOf` utility using `AnyOf`:

```scala
import sttp.tapir.json.circe.jsonBody

object anyOf extends AnyOf[MyError](jsonBody)
```

> You don't necessarily have to use `jsonBody`, you can use any of the available output types.

### Use it when creating your endpoint :tada:

```scala
import sttp.tapir._

val myEndpoint = endpoint.get
  .in("v1" / "users" / path[String]("id"))
  .out(stringBody)
  .errorOut(anyOf[UserNotFound, WrongUser, WrongPassword])
```

> **Important:** remember that some of this utilities throw runtime exceptions, so you should ensure this won't fail on runtime by creating a simple test where you use the endpoint. If the `anyOf` call fails, the instantiation of the endpoint will fail.

<details><summary>See generated OpenApi</summary>

```yaml
paths:
  /v1/users/{id}:
    get:
      operationId: getV1UsersId
      parameters:
      - name: id
        in: path
        required: true
        schema:
          type: string
      responses:
        '200':
          content:
            text/plain:
              schema:
                type: string
        '403':
          content:
            application/json:
              schema:
                oneOf:
                - $ref: '#/components/schemas/WrongPassword'
                - $ref: '#/components/schemas/WrongUser'
                discriminator:
                  propertyName: error
                  mapping:
                    wrong-password: '#/components/schemas/WrongPassword'
                    wrong-user: '#/components/schemas/WrongUser'
        '404':
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserNotFound'
components:
  schemas:
    UserNotFound:
      required:
      - name
      - error
      type: object
      properties:
        name:
          type: string
        error:
          type: string
          enum:
          - user-not-found
    WrongPassword:
      required:
      - id
      - error
      type: object
      properties:
        id:
          type: string
        error:
          type: string
          enum:
          - wrong-password
    WrongUser:
      required:
      - id
      - error
      type: object
      properties:
        id:
          type: string
        error:
          type: string
          enum:
          - wrong-user
```

</details>
