import cats.data.Validated.{Invalid, Valid}
import cats.effect.{IO, IOApp}
import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection._
import eu.timepit.refined.string.MatchesRegex

object MainRefined extends IOApp.Simple {

  private type Name = String Refined NonEmpty
  private type Age = Int Refined GreaterEqual[18]
  private type Email = String Refined MatchesRegex["^[A-z0-9._%+-]+@[A-z0-9.-]+\\.[A-z]{2,6}"]

  private case class PersonRefined(name: Name, age: Age, email: Email)

  def run: IO[Unit] = {
    for {
      _ <- IO.println("Hello world!")
      _ <- IO.println("Enter name")
      string <- IO.readLine
      _ <- IO.println("Enter age (minimum 18 years of age)")
      inputAge <- IO.readLine
      _ <- IO.println("Enter email")
      userEmail <- IO.readLine
      result <- validatePerson(string, inputAge.toInt, userEmail)
      _ <- IO.println(s"Person name: ${result.name} age: ${result.age} email: ${result.email}")
    } yield ()
  } handleError (e => println(s"\n\nFailure caused by ${e.getMessage}"))

  private def validatePerson(input: String, inputAge: Int, inputEmail: String): IO[PersonRefined] = {

    val maybeName: Either[String, Name] = refineV(input)
    val maybeAge: Either[String, Age] = refineV(inputAge)
    val maybeEmail: Either[String, Email] = refineV(inputEmail)

    import cats.implicits._

    val validated =
      (maybeName.toValidatedNec, maybeAge.toValidatedNec, maybeEmail.toValidatedNec) mapN { (name, age, email) =>
        PersonRefined(name, age, email)
    }

    validated match {
      case Valid(person) => IO.pure(person)
      case Invalid(e) =>
        IO.raiseError(new Exception(e.mkString_("errors:\nerror: ",",\nerror: " ,"")))
    }
  }
}