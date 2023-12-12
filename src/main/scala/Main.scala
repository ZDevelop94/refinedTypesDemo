import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {

  private lazy val emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}".r

  def run: IO[Unit] = {
    for {
      _ <- IO.println("Hello world!")
      _ <- IO.println("Enter name")
      string <- IO.readLine
      _ <- IO.println("Enter age (minimum 18 years of age")
      inputAge <- IO.readLine
      _ <- IO.println("Enter email")
      userEmail <- IO.readLine
      result <- validateTraditionally(string, inputAge.toInt, userEmail)
      _ <- IO.println(s"Person name: ${result.name} age: ${result.age} email: ${result.email}")
    } yield ()
  }

  private def validateTraditionally(input: String, intMaybePositive: Int, inputEmail: String): IO[Person] = {

    val isValidName = input.nonEmpty
    val isValidAge = intMaybePositive >= 18
    val isValidEmail = emailRegex.matches(inputEmail)

    if (isValidName && isValidAge && isValidEmail) {
      IO(Person(input, intMaybePositive, inputEmail))
    } else {
      IO.raiseError[Person](new Exception(""))
    }
  }
}

case class Person(name: String, age: Int, email: String)