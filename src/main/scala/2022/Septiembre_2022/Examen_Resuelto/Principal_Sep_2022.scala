package viajeTren

object Principal_Sep_2022 {
  def main(args: Array[String]): Unit = {
    val tren = new Tren()
    val m = new Maquinista(tren)
    val pas = Array.tabulate(20)(i => new Pasajero(tren, i))

    m.start()
    pas.foreach(_.start())
  }
}
