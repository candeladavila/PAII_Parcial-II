
object PruebaConvoy {
  def main(args: Array[String]): Unit = {
    val tamaño = 10
    val convoy = new Convoy(10)
    val flota = Array.tabulate(tamaño)(i => new Furgoneta(i, convoy))
    //iniciamos la flota con las 10 furgonetas
    flota.foreach(_.start())
  }
}
