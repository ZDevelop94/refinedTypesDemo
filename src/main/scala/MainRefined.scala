import cats.effect.{IO, IOApp}
import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection._
import eu.timepit.refined.string.{MatchesRegex, Regex}

object MainRefined extends IOApp.Simple {

  private type Name = String Refined NonEmpty
  private type Age = Int Refined NonNegative
  private type Email = String Refined MatchesRegex["^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}"]

  private case class PersonRefined(name: Name, age: Age, email: Email)

  def run: IO[Unit] = {
    for {
      _ <- IO.println("Hello world!")
      _ <- IO.println("Enter name")
      string <- IO.readLine
      _ <- IO.println("Enter age (minimum 18 years of age")
      inputAge <- IO.readLine
      _ <- IO.println("Enter email")
      userEmail <- IO.readLine
      result <- validateRefined(string, inputAge.toInt, userEmail)
      _ <- IO.println(s"Person name: ${result.name} age: ${result.age} email: ${result.email}")
    } yield ()
  }

  private def validateRefined(input: String, inputAge: Int, inputEmail: String): IO[PersonRefined] = {

    lazy val error = (message: String) => IO.raiseError(new Exception(message))

    val maybeName: Either[String, Name] = refineV(input)
    val maybeAge: Either[String, Age] = refineV(inputAge)
    val maybeEmail: Either[String, Email] = refineV(inputEmail)

    (maybeName, maybeAge, maybeEmail) match {
      case (Right(name), Right(age), Right(email)) => IO.pure(PersonRefined(name, age, email))
      case (Left(badName), Right(_), Right(_)) => error(s"badName: $badName")
      case (Right(_), Left(badAge), Right(_)) => error(s"badAge: $badAge")
      case (Right(_), Right(_), Left(badEmail)) => error(s"badEmail: $badEmail")
    }
  }
}