import java.util.concurrent._

class Convoy(tam: Int) {

  //VARIABLES
  private var numFurgonetas = 0 //inicialmente no hay furgonetas en el convoy
  private val mutex = new Semaphore(1) //semáforo para controlar numFurgonetas
  private var idLider = -1 //inicialmente no hay ningún id asociado al líder
  private val entradaConvoy = new Semaphore(1) //inicialmente no hay nadie en el convoy
  private val salidaConvoy = new Semaphore (0) //inicialmente no se puede salir nadie
  private val empiezaViaje = new Semaphore(0) //inicialmente no se inicia ningún viaje
  private val liberaLider = new Semaphore(0) //inicialmente no se puede liberar al líder

  def unir(id: Int): Int = {
    entradaConvoy.acquire()
    mutex.acquire()
    numFurgonetas +=1
    if (numFurgonetas == 1){ //si es la primera en llegar se convierte en lider
      idLider = id
      println(s"** Furgoneta $id lidera del convoy **")
      entradaConvoy.release()
    } else { //si no es la primera
      println(s"Furgoneta $id seguidora")
      if (numFurgonetas == tam) { //es la ultima
        empiezaViaje.release()
      } else{
        entradaConvoy.release()
      }
    }
    mutex.release()
    idLider
  }

  def calcularRuta(id: Int): Unit = {
    empiezaViaje.acquire()
    println(s"** Furgoneta $id lider:  ruta calculada, nos ponemos en marcha **")
    salidaConvoy.release()
  }

  def destino(id: Int): Unit = {
    liberaLider.acquire()
    mutex.acquire()
    numFurgonetas -=1 //libera al lider (numFurgonetas = 0)
    println(s"** Furgoneta $id lider abandona el convoy **")
    entradaConvoy.release() //permite la entrada de nuevo a las furgonetas para formar un nuevo convoy
  }

  def seguirLider(id: Int): Unit = {
    salidaConvoy.acquire()
    mutex.acquire()
    if (numFurgonetas == 10) { //están todas
      println(s"** Furgoneta $idLider lider:  hemos llegado al destino **")
    }
    numFurgonetas -=1
    println(s"Furgoneta $id abandona el convoy")
    if (numFurgonetas >=2) {
      salidaConvoy.release()
    } else {
      liberaLider.release()
    }
    mutex.release()
  }
}
