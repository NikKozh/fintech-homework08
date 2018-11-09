package fintech.homework08

import java.time.LocalDate

// Я написал небольшое API для работы с базами данных через jdbc (DBRes.scala)
// и использовал его для написания PeopleApp
// К сожалению когда я запускаю приложение я вижу что оно коннектится к базе много раз

// Добавьте в DBRes методы map и flatMap и перепешите код используя for
// и выполняя execute только один раз

object PeopleApp extends PeopleModule {
  val uri = "jdbc:h2:~/dbres"

  def getOldPerson: DBResOp[Person] =
    DBRes.select("SELECT * FROM people WHERE birthday < ?",
                 List(LocalDate.of(1979, 2, 20)))(readPerson).map(_.head)

  def clonePerson(person: Person): DBResOp[Person] = {
    val clone = person.copy(birthday = LocalDate.now())
    storePerson(clone).map(_ => clone)
  }

  def program: DBResOp[Person] = {
    for {
      _ <- setup(uri)
      old <- getOldPerson
      clone <- clonePerson(old)
    } yield clone
  }

  def main(args: Array[String]): Unit = {
    val result = DBRes(program.operation).execute(uri)
    println(result)
  }
}
