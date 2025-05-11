import java.util.concurrent.Semaphore
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable

class DatoCompartido(private val nProcesadores: Int) {
  //VARIABLES INICIALES
  // Dato a procesar
  private var dato: Int = 0
  // Número de procesadores totales = nProcesadores (parámetro)
  // Número de procesadores pendientes de procesar el dato
  private var procPend: Int = 0

  /**
   * Recibe como parámetro el número de procesadores que tienen que manipular
   * cada dato generado. Debe ser un número mayor que 0.
   */

  //VARIABLES
  private val mutex = new Semaphore(1) //semáforo para controlar las variables dato y procPend
  private val generar = new Semaphore(1) // 1 = genera -> 0 = procesa
  private val finProcesamiento = new Semaphore(0) //inicialmente no hay ningún procesamiento terminado
  private val leerDato = new Semaphore(0) //inicialmente no se puede leer ningún dato
  private val procesarDato = new Semaphore(0) //inicialmente no se puede procesar ningún dato
  private val procesadoresUsados: mutable.Buffer[Int] = new ArrayBuffer[Int]()

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
    generar.acquire()
    mutex.acquire()
    dato = d //nuevo dato
    println(s"Dato a procesar: $dato")
    procPend = nProcesadores //reiniciamos el contador de procesadores
    procesadoresUsados.clear() //reiniciamos los procesadores
    println(s"Número de procesadores pendientes: $procPend")
    //INICIO PROCESAMIENTO
    leerDato.release() //liberamos los procesadores
    mutex.release()
    //FIN PROCESAMIENTO
    finProcesamiento.acquire()
    mutex.acquire()
    val resultado = dato
    generar.release() //volvemos a generar un dato
    mutex.release()
    resultado
  }

  /**
   * El Procesador con identificador id utiliza este método para leer el
   * dato que debe procesar (el dato se devuelve como valor de retorno del método).
   * Deberá esperarse si no hay datos nuevos para procesar
   * o si otro procesador está manipulando el dato.
   *
   * CS1_Procesador: espera si no hay un nuevo dato que procesar.
   * CS2_Procesador: espera a que el dato esté disponible para procesarlo.
   */
  def leeDato(id: Int): Int = {
    leerDato.acquire()
    procesarDato.release()
    dato
  }

  /**
   * El Procesador con identificador id almacena en el recurso compartido el resultado
   * de haber procesado el dato. Una vez hecho esto actuará de una de las dos formas siguientes:
   * (1) Si aún hay procesadores esperando a procesar el dato los avisará.
   * (2) Si él era el último procesador avisará al Generador de que han terminado.
   */
  def actualizaDato(id: Int, datoActualizado: Int): Unit = {
    procesarDato.acquire()
    mutex.acquire()
    if (!procesadoresUsados.contains(id)) { //EL PROCESADOR NO HA PROCESADO ANTES
      println(s"\tProcesador $id ha procesado el dato. Nuevo dato: $datoActualizado")
      dato = datoActualizado
      procPend -= 1
      procesadoresUsados += id
      println(s"Número de procesadores pendientes: $procPend")
      if (procPend == 0) {
        finProcesamiento.release()
      } else {
        leerDato.release()
      }
      mutex.release()
    } else { //EL PROCESADOR YA HABÍA PROCESADO
      if (procPend != 0){
        leerDato.release()
      }
      mutex.release()
    }
  }
}
