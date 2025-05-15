import java.util.concurrent._

class Tiovivo(capacidad: Int) {
  //VARIABLES
  private var nPasajeros = 0
  private val mutex = new Semaphore(1)
  private val puertaEntrada = new Semaphore(1) //inicialmente el tiovivo está vacio y se puede entrar
  private val puertaSalida = new Semaphore(0) //inicialmente la puerta de salida está cerrada
  private val lleno = new Semaphore(0) //inicialmente está vacio, se libera cuando se llene

  def subir(id: Int): Unit = {
    puertaEntrada.acquire()
    mutex.acquire()
    nPasajeros += 1
    println(s"El pasajero $id se sube. Quedan ${capacidad-nPasajeros} plazas.")
    if (nPasajeros < capacidad){ //si quedan plazas libres
      puertaEntrada.release()
    } else{
      lleno.release()
    }
    mutex.release()
  }

  def bajar(id: Int): Unit = {
    puertaSalida.acquire()
    mutex.acquire()
    nPasajeros -= 1
    println(s"El pasajero $id se baja. Quedan ${nPasajeros}.")
    if (nPasajeros > 0){
      puertaSalida.release()
    } else{
      println("************************************************")
      puertaEntrada.release()
    }
    mutex.release()
  }

  def esperaLleno(): Unit = {
    lleno.acquire()
    println("TIOVIVO LLENO, EMPIEZA EL VIAJE!!!")
  }

  def finViaje(): Unit = {
    println("FIN DEL VIAJE :(")
    puertaSalida.release()
  }
}
