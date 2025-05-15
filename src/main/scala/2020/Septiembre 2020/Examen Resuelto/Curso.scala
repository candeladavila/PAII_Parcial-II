import java.util.concurrent._
class Curso {

  // Número máximo de alumnos cursando simultáneamente la parte de iniciación
  private val MAX_ALUMNOS_INI = 10

  // Número de alumnos por grupo en la parte avanzada
  private val ALUMNOS_AV = 3

  //VARIABLES
  private var alumnos_ini = 0
  private var esperando_grupo = 0
  private var terminado_proyecto = 0

  private val mutex = new Semaphore(1)
  private val inicio_ini = new Semaphore(1) //inicialmente se puede acceder al curso de iniciación (quedan plazas)
  private val inicio_ava = new Semaphore(0) //inicialmente nadie puede acceder a avanzada
  private val espera_grupo = new Semaphore(1)
  private val espera_fin_proyecto = new Semaphore(0)


  // El alumno tendrá que esperar si ya hay 10 alumnos cursando la parte de iniciación
  @throws[InterruptedException]
  def esperaPlazaIniciacion(id: Int): Unit = {
    inicio_ini.acquire()
    mutex.acquire()
    alumnos_ini += 1
    // Mensaje a mostrar cuando el alumno pueda conectarse y cursar la parte de iniciación
    println(s"PARTE INICIACION: Alumno $id cursa parte iniciacion")
    if (alumnos_ini < MAX_ALUMNOS_INI){ //quedan plazas libres -> deja abierta la puerta
      inicio_ini.release()
    }
    mutex.release()
  }

  // El alumno informa que ya ha terminado de cursar la parte de iniciación
  @throws[InterruptedException]
  def finIniciacion(id: Int): Unit = {
    mutex.acquire()
    alumnos_ini -= 1
    // Mensaje a mostrar para indicar que el alumno ha terminado la parte de principiantes
    println(s"PARTE INICIACION: Alumno $id termina parte iniciacion")
    if (alumnos_ini == MAX_ALUMNOS_INI-1){ //es el primero en salir, abre la puerta para que pueda salir otro
      inicio_ini.release()
    }
    mutex.release()
  }

  /* El alumno tendrá que esperar:
   *   - si ya hay un grupo realizando la parte avanzada
   *   - si todavía no están los tres miembros del grupo conectados
   */
  @throws[InterruptedException]
  def esperaPlazaAvanzado(id: Int): Unit = {
    espera_grupo.acquire() //espera a que lleguen los que falten del grupo para empezar
    mutex.acquire()
    esperando_grupo += 1
    // Mensaje a mostrar si el alumno tiene que esperar al resto de miembros en el grupo
    println(s"PARTE AVANZADA: Alumno $id espera a que haya $ALUMNOS_AV alumnos")
    if (esperando_grupo < ALUMNOS_AV){ //si todavía no son suficientes para empezar el proyecto
      //dejamos al siguiente alumno entrar
      espera_grupo.release()
      //bloqueo para esperar a los otros dos compañeros
      mutex.release()
      espera_fin_proyecto.acquire() //se bloquea aquí para esperar al resto
      mutex.acquire() //vuelvo a coger el mutex cuando me he desbloqueado
    } else{ //esperando_grupo == ALUMNOS_AV
      // Mensaje a mostrar cuando el alumno pueda empezar a cursar la parte avanzada
      println(s"PARTE AVANZADA: Hay $esperando_grupo alumnos. Alumno $id empieza el proyecto")
    }
    esperando_grupo -= 1
    if (esperando_grupo != 0){
      espera_fin_proyecto.release() //desbloqueamos a los tres alumnos
    }
    mutex.release()
  }

  /* El alumno:
   *   - informa que ya ha terminado de cursar la parte avanzada
   *   - espera hasta que los tres miembros del grupo hayan terminado su parte
   */
  @throws[InterruptedException]
  def finAvanzado(id: Int): Unit = {
    // Espera a que los 3 alumnos terminen su parte avanzada
    mutex.acquire()
    terminado_proyecto += 1 //termina su parte del proyecto
    // Mensaje a mostrar si el alumno tiene que esperar a que los otros miembros del grupo terminen
    println(s"PARTE AVANZADA: Alumno $id termina su parte del proyecto. Espera al resto")
    if (terminado_proyecto < ALUMNOS_AV){ //si aún no han terminado todos
      mutex.release()
      espera_fin_proyecto.acquire() //se bloquea aquí hasta que termine el resto
    } else { //si ya han terminado todos
      println(s"PARTE AVANZADA: LOS $terminado_proyecto ALUMNOS HAN TERMINADO EL CURSO") //aquí alummnos_ava == ALUMNOS_AV
    }
    terminado_proyecto -= 1 //termina
    if (terminado_proyecto != 0){ //desbloqueamos en cascada
      espera_fin_proyecto.release()
    } else {
      espera_grupo.release()
    }
    mutex.release()
  }
}
