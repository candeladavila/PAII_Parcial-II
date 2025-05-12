class Curso {

  // Número máximo de alumnos cursando simultáneamente la parte de iniciación
  private val MAX_ALUMNOS_INI = 10

  // Número de alumnos por grupo en la parte avanzada
  private val ALUMNOS_AV = 3

  // El alumno tendrá que esperar si ya hay 10 alumnos cursando la parte de iniciación
  @throws[InterruptedException]
  def esperaPlazaIniciacion(id: Int): Unit = {
    // Espera si ya hay 10 alumnos cursando esta parte

    // Mensaje a mostrar cuando el alumno pueda conectarse y cursar la parte de iniciación
    println(s"PARTE INICIACION: Alumno $id cursa parte iniciacion")
  }

  // El alumno informa que ya ha terminado de cursar la parte de iniciación
  @throws[InterruptedException]
  def finIniciacion(id: Int): Unit = {
    // Mensaje a mostrar para indicar que el alumno ha terminado la parte de principiantes
    println(s"PARTE INICIACION: Alumno $id termina parte iniciacion")

    // Libera la conexión para que otro alumno pueda usarla
  }

  /* El alumno tendrá que esperar:
   *   - si ya hay un grupo realizando la parte avanzada
   *   - si todavía no están los tres miembros del grupo conectados
   */
  @throws[InterruptedException]
  def esperaPlazaAvanzado(id: Int): Unit = {
    // Espera a que no haya otro grupo realizando esta parte

    // Espera a que haya tres alumnos conectados

    // Mensaje a mostrar si el alumno tiene que esperar al resto de miembros en el grupo
    println(s"PARTE AVANZADA: Alumno $id espera a que haya $ALUMNOS_AV alumnos")

    // Mensaje a mostrar cuando el alumno pueda empezar a cursar la parte avanzada
    println(s"PARTE AVANZADA: Hay $ALUMNOS_AV alumnos. Alumno $id empieza el proyecto")
  }

  /* El alumno:
   *   - informa que ya ha terminado de cursar la parte avanzada
   *   - espera hasta que los tres miembros del grupo hayan terminado su parte
   */
  @throws[InterruptedException]
  def finAvanzado(id: Int): Unit = {
    // Espera a que los 3 alumnos terminen su parte avanzada

    // Mensaje a mostrar si el alumno tiene que esperar a que los otros miembros del grupo terminen
    println(s"PARTE AVANZADA: Alumno $id termina su parte del proyecto. Espera al resto")

    // Mensaje a mostrar cuando los tres alumnos del grupo han terminado su parte
    println(s"PARTE AVANZADA: LOS $ALUMNOS_AV ALUMNOS HAN TERMINADO EL CURSO")
  }
}
