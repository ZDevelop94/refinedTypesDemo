import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.auto._
import eu.timepit.refined.refineMV

object SimpleRefined {

  private type Name = String Refined NonEmpty

  //val name: Name = ""
  //val bob: Name = refineMV("")

}
