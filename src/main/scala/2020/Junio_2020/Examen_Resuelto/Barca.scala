package Barco
import java.util.concurrent.*

class Barca {
  //VARIABLES
  private val capacidadBarco = 3 //información del enunciado
  private var orilla = 1 //inicialmente la barca está al norte (norte = 1, sur = 0)
  private var nViajeros = 0 //inicialmente no hay pasajeros en la barca
  private val mutex = new Semaphore(1) //controla el acceso a nViajeros
  private val puertaEntrada = Array.fill(2)(new Semaphore(0)) // inicializa ambos con 0 semáforos
  private val puertaSalida = new Semaphore(0) //inicialmente nadie puede salir
  private val inicioViaje = new Semaphore(0) //inicialmente no hay viajes iniciados
  private val finalViaje = new Semaphore(0) //inicialmente no hay viajes finalizados
  puertaEntrada(1).release() //inicialmente el semáforo del norte está abierto
  /*
   * El Pasajero id quiere darse una vuelta en la barca desde la orilla pos
   */
  @throws[InterruptedException]
  def subir(id: Int, pos: Int): Unit = {
    puertaEntrada(pos).acquire() //miramos el semáforo que corresponda a nuestra orilla
    mutex.acquire()
    nViajeros += 1
    println(s"Viajero $id se sube al barco en la orilla $pos")
    if (nViajeros < capacidadBarco){ //aún queda espacio en el barco
      puertaEntrada(pos).release()
    } else{ //si es el último pasajero en entrar al barco (lo llena)
      inicioViaje.release()
    }
    mutex.release()
  }

  /*
   * Cuando el viaje ha terminado, el Pasajero que está en la barca se baja
   */
  @throws[InterruptedException]
  def bajar(id: Int): Int = {
    puertaSalida.acquire()
    mutex.acquire()
    nViajeros -= 1 //sale un viajero
    println(s"Viajero $id baja del barco en la orilla $orilla")
    if (nViajeros > 0){ //siguen quedando viajeros
      puertaSalida.release()
      mutex.release()
    } else{ //es el último viajero
      println("**********************************************")
      puertaEntrada(orilla).release()
      mutex.release()
    }
    orilla //devuelve la orilla
  }

  /*
   * El Capitán espera hasta que se suben 3 pasajeros para comenzar el viaje
   */
  @throws[InterruptedException]
  def esperoSuban(): Unit = {
    inicioViaje.acquire()
    println("Empieza el viaje!!!")
    finalViaje.release()
  }

  /*
   * El Capitán indica a los pasajeros que el viaje ha terminado y tienen que bajarse
   */
  @throws[InterruptedException]
  def finViaje(): Unit = {
    finalViaje.acquire()
    mutex.acquire()
    orilla = (orilla+1)%2 //cambiamos la orilla
    println("Fin del viaje!!!")
    puertaSalida.release()
    mutex.release()
  }

}
