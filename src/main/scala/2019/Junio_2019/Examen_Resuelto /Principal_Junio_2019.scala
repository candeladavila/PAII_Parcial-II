
object Principal_Junio_2019 {
  def main(args: Array[String]): Unit = {
    val t = new Tiovivo(5)
    val o = new Operario(t)
    val personas = Array.tabulate(7)(i => new Pasajero(i, t))

    personas.foreach(_.start())
    o.start()
  }
}
