package fintech.homework08

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import scala.collection.mutable.ListBuffer

case class DBRes[A](run: Connection => A) {
  def map[B](f: A => B): DBRes[B] = DBRes(conn => f(run(conn)))

  def flatMap[B](f: A => DBRes[B]): DBRes[B] =
    DBRes(conn => f(run(conn)).run(conn))
  
  def execute(uri: String): A = {
    DBRes.connectCount += 1 // только для теста

    println("Opening connection to DB...")
    val conn = DriverManager.getConnection(uri)
    val res = run(conn)
    conn.close()
    println("Closing connection to DB...")
    res
  }
}

object DBRes {
  var connectCount = 0 // только для теста

  def select[A](sql: String, params: Seq[Any])
               (read: ResultSet => A): DBRes[List[A]] = DBRes { conn =>
    println(s"Calling select method with sql: $sql, params: $params")
    val rs = prepare(sql, params, conn).executeQuery()
    readResultSet(rs, read)
  }

  def update(sql: String, params: Seq[Any]): DBRes[Unit] = DBRes { conn =>
    println(s"Calling update method with sql: $sql, params: $params")
    prepare(sql, params, conn).executeUpdate()
  }

  private def prepare(sql: String, params: Seq[Any], conn: Connection): PreparedStatement = {
    val ps = conn.prepareStatement(sql)

    for ((p, i) <- params.zipWithIndex)
      ps.setObject(i + 1, p)

    ps
  }

  private def readResultSet[A](rs: ResultSet, read: ResultSet => A): List[A] = {
    val buffer = new ListBuffer[A]
    while(rs.next()) {
      buffer.append(read(rs))
    }
    buffer.toList
  }
}