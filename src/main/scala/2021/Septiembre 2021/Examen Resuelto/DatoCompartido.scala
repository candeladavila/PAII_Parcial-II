import java.util.concurrent.Semaphore
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable

class DatoCompartido(private val nProcesadores: Int) {
  //VARIABLES INICIALES
  // Dato a procesar
  private var dato: Int = 0
  // Número de procesadores pendientes
  private var nProc = 0
  /**
   * Recibe como parámetro el número de procesadores que tienen que manipular
   * cada dato generado. Debe ser un número mayor que 0.
   */

  //VARIABLES
  private val mutex_dato = new Semaphore(1) //sobre la variable dato
  private val mutex_proc = new Semaphore(1) //sobre la variable procPend
  private val procesadores = Array.fill(10)(new Semaphore(0)) //creamos un array de tamaño 10 con los semáforos de los 10 procesadores
  private val genera = new Semaphore(1) //inicialmente se puede generar
  private val finProcesamiento = new Semaphore(0) //inicialmente no se ha terminado ningún procesamiento
  private val leer = new Semaphore(0) //inicialmente nadie puede leer

  /**
   * El Generador utiliza este método para almacenar un nuevo dato a procesar.
   * Una vez almacenado el dato se debe avisar a los procesadores de que se ha
   * almacenado un nuevo dato.
   *
   * Por último, el Generador tendrá que esperar en este método a que todos los
   * procesadores terminen de procesar el dato.
   *
   * CS_Generador: espera a que todos los procesadores terminen antes de generar el siguiente dato.
   */

  def generaDato(d: Int): Int = {
    genera.acquire()
    mutex_dato.acquire()
    mutex_proc.acquire()
    dato = d //nuevo dato generado
    println(s"Dato a procesar: $dato")
    nProc = nProcesadores //reiniciamos el número de procesadores pendientes
    println(s"Número de procesadores pendientes: $nProc")
    //PROCESAMOS
    mutex_dato.release()
    mutex_proc.release()
    leer.release()
    procesadores.foreach(_.release()) //liberamos a todos los procesadores
    //FIN PROCESAMIENTO
    finProcesamiento.acquire()
    mutex_dato.acquire()
    var datoFinal = dato
    mutex_dato.release()
    genera.release()
    datoFinal
  }

  /**
   * El Procesador con identificador id utiliza este método para leer el
   * dato que debe procesar (el dato se devuelve como valor de re
   * torno del método).
   * Deberá esperarse si no hay datos nuevos para procesar
   * o si otro procesador está manipulando el dato.
   *
   * CS1_Procesador: espera si no hay un nuevo dato que procesar.
   * CS2_Procesador: espera a que el dato esté disponible para procesarlo.
   */
  def leeDato(id: Int): Int = {
    procesadores(id).acquire()
    leer.acquire()
    mutex_dato.acquire()
    var datoActual = dato
    mutex_dato.release()
    datoActual
  }

  /**
   * El Procesador con identificador id almacena en el recurso compartido el resultado
   * de haber procesado el dato. Una vez hecho esto actuará de una de las dos formas siguientes:
   * (1) Si aún hay procesadores esperando a procesar el dato los avisará.
   * (2) Si él era el último procesador avisará al Generador de que han terminado.
   */
  def actualizaDato(id: Int, datoActualizado: Int): Unit = {
    mutex_dato.acquire()
    mutex_proc.acquire()
    dato = datoActualizado
    println(s"\tProcesador $id ha procesado el dato. Nuevo dato: $dato")
    nProc -=1 //restamos 1 a los procesadores pendientes
    println(s"Numero de procesadores pendientes: $nProc")
    if (nProc > 0){ //quedan procesadores pendientes
      mutex_dato.release()
      mutex_proc.release()
      leer.release()
    }else{ //ya han procesado todos los procesadores
      mutex_dato.release()
      mutex_proc.release()
      finProcesamiento.release() //liberamos al fin del generador
    }
  }
}
