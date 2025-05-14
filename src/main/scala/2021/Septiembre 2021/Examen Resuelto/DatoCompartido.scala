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
  private val mutex_dato = new Semaphore(1) //mutex sobre la variable dato
  private val mutex_proc = new Semaphore(0)

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
    println(s"Dato a procesar: $dato")
    println(s"Número de procesadores pendientes: $procPend")
    0
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
    0
  }

  /**
   * El Procesador con identificador id almacena en el recurso compartido el resultado
   * de haber procesado el dato. Una vez hecho esto actuará de una de las dos formas siguientes:
   * (1) Si aún hay procesadores esperando a procesar el dato los avisará.
   * (2) Si él era el último procesador avisará al Generador de que han terminado.
   */
  def actualizaDato(id: Int, datoActualizado: Int): Unit = {

  }
}
