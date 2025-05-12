import java.util.concurrent._
class Curso {

  // Número máximo de alumnos cursando simultáneamente la parte de iniciación
  private val MAX_ALUMNOS_INI = 10

  // Número de alumnos por grupo en la parte avanzada
  private val ALUMNOS_AV = 3

  //VARIABLES
  private var alumnosIniciacion = 0 //inicialmente no hay alumnos
  private var alumnosGrupo = 0 //inicialmente no hay alumnos
  private val mutex1 = new Semaphore(1) //acesso a la variable alumnosIniciacion
  private val mutex2 = new Semaphore(1) //acceso a la variable ALUMNOS_AV
  private val inicioIniciacion = new Semaphore(1) //inicialmente se puede entrar a iniciación
  private val finIniciacion = new Semaphore(0) //inicialmente nadie termina iniciación
  private val esperaAvanzada = new Semaphore(0) //inicialmente nadie empieza avanzada
  private val esperaGrupo = new Semaphore(1) //inicialmente no hay ningún grupo
  private val inicioProyecto = new Semaphore(0) //inicialmente no hay ningún proyecto empezado en avanzada
  private val finProyecto = new Semaphore(0) //inicialmente nadie termina avanzada

  // El alumno tendrá que esperar si ya hay 10 alumnos cursando la parte de iniciación
  @throws[InterruptedException]
  def esperaPlazaIniciacion(id: Int): Unit = {
    // Espera si ya hay 10 alumnos cursando esta parte
    inicioIniciacion.acquire()
    mutex1.acquire()
    alumnosIniciacion +=1
    // Mensaje a mostrar cuando el alumno pueda conectarse y cursar la parte de iniciación
    println(s"PARTE INICIACION: Alumno $id cursa parte iniciacion")
    finIniciacion.release() //dejamos que finalice
    if (alumnosIniciacion < MAX_ALUMNOS_INI){ //si quedan plazas libres deja al resto entrar
      inicioIniciacion.release()
    } //si no quedan plazas libres entonces no libera nada
    mutex1.release()
  }

  // El alumno informa que ya ha terminado de cursar la parte de iniciación
  @throws[InterruptedException]
  def finIniciacion(id: Int): Unit = {
    finIniciacion.acquire()
    mutex1.acquire()
    alumnosIniciacion -= 1 //termina iniciación y deja su plaza libre
    // Mensaje a mostrar para indicar que el alumno ha terminado la parte de principiantes
    println(s"PARTE INICIACION: Alumno $id termina parte iniciacion")
    // Libera la conexión para que otro alumno pueda usarla
    inicioIniciacion.release()
    mutex1.release()
    // Inicia el proceso de espera para la plaza de avanzada
    esperaAvanzada.release() //empieza a esperar
  }

  /* El alumno tendrá que esperar:
   *   - si ya hay un grupo realizando la parte avanzada
   *   - si todavía no están los tres miembros del grupo conectados
   */
  @throws[InterruptedException]
  def esperaPlazaAvanzado(id: Int): Unit = {
    // Espera a que no haya otro grupo realizando esta parte
    esperaAvanzada.acquire()
    mutex2.acquire()
    esperaGrupo.acquire() //primero tiene que ver si hay alguien que ya esté dentro de avanzada o no
    // Espera a que haya tres alumnos conectados
    alumnosGrupo += 1 //se suma al grupo
    // Mensaje a mostrar si el alumno tiene que esperar al resto de miembros en el grupo
    println(s"PARTE AVANZADA: Alumno $id espera a que haya $ALUMNOS_AV alumnos")
    if (alumnosGrupo < ALUMNOS_AV){ //faltan componentes para empezar el grupo
      mutex2.release()
      esperaGrupo.release() //deja que entren más alumnos
    } else{ //ya están todos los componentes -> empieza el proyecto
      // Mensaje a mostrar cuando el alumno pueda empezar a cursar la parte avanzada
      println(s"PARTE AVANZADA: Hay $ALUMNOS_AV alumnos. Alumno $id empieza el proyecto")
      mutex2.release()
      inicioProyecto.release()
    }
  }

  /* El alumno:
   *   - informa que ya ha terminado de cursar la parte avanzada
   *   - espera hasta que los tres miembros del grupo hayan terminado su parte
   */
  @throws[InterruptedException]
  def finAvanzado(id: Int): Unit = {
    // Espera a que los 3 alumnos terminen su parte avanzada
    inicioProyecto.acquire()
    mutex2.acquire()
    alumnosGrupo -= 1 //actualiza el índice de personas pendientes por terminar
    // Mensaje a mostrar si el alumno tiene que esperar a que los otros miembros del grupo terminen
    println(s"PARTE AVANZADA: Alumno $id termina su parte del proyecto. Espera al resto")
    if (alumnosGrupo > 0){ //quedan compañeros por terminar -> vuelve a abrir la puerta de inicioProyecto
      inicioProyecto.release()
    } else{ //es el último en terminar
      // Mensaje a mostrar cuando los tres alumnos del grupo han terminado su parte
      println(s"PARTE AVANZADA: LOS $ALUMNOS_AV ALUMNOS HAN TERMINADO EL CURSO")
      esperaGrupo.release() //nuevo grupo
    }
    mutex2.release()
  }
}
