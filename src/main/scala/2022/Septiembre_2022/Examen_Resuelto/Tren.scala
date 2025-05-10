package viajeTren

class Tren {

  @throws[InterruptedException]
  def viaje(id: Int): Unit = {
    // Lógica del viaje del pasajero (a completar si es necesario)
  }

  @throws[InterruptedException]
  def empiezaViaje(): Unit = {
    println("        Maquinista:  empieza el viaje")
  }

  @throws[InterruptedException]
  def finViaje(): Unit = {
    println("        Maquinista:  fin del viaje")
  }
}
