package akka.apps

trait Show[A] {
  def show(a: A): String
}

object test_app {

  private def show[A](a: A)(implicit sh: Show[A]) = sh.show(a)

  final def main(args: Array[String]): Unit = {

    implicit val sh1 : Show[Int] = (a: Int) => "Int: " + a.toString
    implicit val sh2 : Show[String] = (a: String) => "Str: " +  a

    println(show(123))
    println(show("123"))


    val collection1 = "Line1"::"Line2"::"Line3"::"Line3"::Nil
    val collection2 = "Line1"::"Line2"::"Line3"::"Line3"::Nil


  }

}
