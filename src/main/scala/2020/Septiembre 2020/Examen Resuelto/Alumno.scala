import scala.util.Random

class Alumno(id: Int, curso: Curso) extends Thread {
  private val rnd = new Random()

  override def run(): Unit = {
    try {
      // El alumno espera un tiempo aleatorio
      Thread.sleep(id * rnd.nextInt(200))

      // El alumno espera para tener una plaza en la parte de iniciación
      curso.esperaPlazaIniciacion(id)

      // El alumno cursa la parte de iniciación, cada uno a su ritmo
      Thread.sleep(id * rnd.nextInt(100))

      // El alumno informa que ha terminado la parte de iniciación y libera su conexión
      curso.finIniciacion(id)

      // El alumno espera a que haya plaza en la parte avanzada y que se forme el grupo de 3 miembros
      curso.esperaPlazaAvanzado(id)

      // El alumno cursa la parte avanzada
      Thread.sleep(id * rnd.nextInt(500))

      // El alumno informa que ha terminado su parte y espera a que todos los miembros del grupo terminen
      curso.finAvanzado(id)
    } catch {
      case e: InterruptedException => e.printStackTrace()
    }
  }
}
