package fintech.homework08
import java.time.LocalDate

import org.scalatest.{FlatSpec, Matchers}

class PeopleAppSpec extends FlatSpec with Matchers {
  import PeopleApp._

  behavior of "People App"

  val result: Person = DBRes(program.operation).execute(uri)

  it should "return right result" in {
    result should be(Person("Alice", LocalDate.now()))
  }

  it should "connect to database only once" in {
    DBRes.connectCount should be(1)
  }
}
