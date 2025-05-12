package viajeTren
import java.util.concurrent.*
import scala.collection.mutable.ArrayBuffer

class Tren {
  /*
  NOTAS:
  - Vamos a hacer el problema suponiendo como en el ejemplo que el tamaño de los vagones es N = 5
    Es decir, en total 10 pasajeros en el tren
   */
  //Variables generales
  private val N = 5 //pasajeros que entran en cada vagón
  private val pasVag1 = ArrayBuffer[Int]() //Array de enteros vacio para almacenar los ids de los pasajeros del vagon1
  private var numPas = 0 //inicialmente no hay pasajeros
  private val mutex = new Semaphore (1) //semáforo para acceder a la variable
  private val puertaEntrada = new Semaphore (1) //inicialmente los vagones están vacíos
  private val lleno = new Semaphore(0) //inicialmente no está lleno
  private val puertaSalida = new Semaphore(0) //inicialmente la puerta de salida está cerrada
  private val esperaVagon2 = new Semaphore(0) //inicialmente la salida del vagón2 está bloqueada (hasta que no baje 1)

  @throws[InterruptedException]
  def viaje(id: Int): Unit = {
    // Lógica del viaje del pasajero
    //ENTRADAS
    puertaEntrada.acquire() //primero miramos si podemos entrar o no al tren (si está lleno)
    mutex.acquire()
    numPas +=1
    //vemos en qué vagón tiene que entrar
    if (numPas < N+1) {
      pasVag1 += id //añadimos el id del pasajero al array de pasajeros del vagón1
      println(s"Pasajero $id ha subido al vagón 1")
    } else{
      println (s"Pasajero $id ha subido al vagón 2")
    }
    //comprobamos si se ha llenado o no el tren
    if (numPas < N*2) { //sigue quedando espacio para más pasajeros
      puertaEntrada.release() //liberamos la puerta
    } else{
      lleno.release() //iniciamos el viaje (despertamos al maquinista)
    }
    mutex.release()

    //SALIDAS
    puertaSalida.acquire() //ha terminado el viaje
    mutex.acquire()
    if (pasVag1.contains(id)){ //estaba en el vagón 1
      numPas -=1 //reducimos el número de pasajeros que quedan
      println(s"Pasajero $id ha bajado del vagón 1")
      if (numPas == N) esperaVagon2.release() //liberamos la bajada del vagón 2 porque ya no quedan más del vagon1
      mutex.release()
      puertaSalida.release() //dejamos que siga bajando gente
    } else{ //estaba en el vagón 2
      esperaVagon2.acquire()
      numPas -= 1
      println(s"Pasajero $id ha bajado del vagón 2")
      if (numPas > 0) { //quedan pasajeros, dejamos la puerta de salida abierta
        esperaVagon2.release()
        puertaSalida.release()
      }
      else {
        println("***************************")
        puertaEntrada.release()
      } //numPas == 0 -> no quedan pasajeros -> nuevo viaje
    }
    mutex.release()
  }

  @throws[InterruptedException]
  def empiezaViaje(): Unit = {
    lleno.acquire()
    println("        Maquinista:  empieza el viaje")
  }

  @throws[InterruptedException]
  def finViaje(): Unit = {
    println("        Maquinista:  fin del viaje")
    puertaSalida.release()
  }
}
