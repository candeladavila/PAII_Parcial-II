import java.util.concurrent._

class Concurso {
  private var nCaras = new Array[Int](2) //creamos un nuevo array de tamaño 2 de enteros para almacenar el número de caras
  private var nJuegos = new Array[Int](2) //creamos un nuevo array de tamaño 2 que almacena el número de juegos ganados por cada jugador
  private var nTiradas = new Array[Int](2) //creamos un nuevo array de tamaño 2 que almacena el número de tirads de cada jugador
  private var juego = 1 //empezamos en el juego 1
  private val mutex = new Semaphore(1)
  private val esperaOtroJugador = new Semaphore(0)
  private val finConcurso = new Semaphore(0)
  private var termina = false //Variable booleana para ver si el concurso ha temrminado o no

  @throws[InterruptedException]
  def tirarMoneda(id: Int, cara: Boolean): Unit = {
    var otroJugador: Int = 1 - id
    mutex.acquire()
    nTiradas(id) += 1 //sumamos una tirada
    if (cara){ //si ha salido cara entonces sumamos uno a la caras
      nCaras(id) += 1 //sumamos una cara al jugador
    }
    //miramos el número de tiradas que llevamos
    if (nTiradas(id) + nTiradas(otroJugador) == 20) { //si ya se han hecho todas las tiradas
      if (nCaras(id) > nCaras(otroJugador)) { //si tenía más caras que el otro jugador gana el juego
        nJuegos(id) += 1 //le sumamos un juego
        println(s"Juego $juego: Ha ganado la partida el jugador $id con ${nCaras(id)}")
      } else if (nCaras(id) < nCaras(otroJugador)) { //gana el otro jugador
        nJuegos((id + 1) % 2) += 1 //le sumamos un juego
        println(s"Juego $juego: Ha ganado la partida el jugador ${otroJugador} con ${nCaras(otroJugador)}")
      } else { //empate
        println(s"Juego $juego: El juego ha empatado")
      }
      //Reiniciamos el número de caras de cada jugador para el nuevo juego
      for (i <- nCaras.indices) {
        nCaras(i) = 0
        nTiradas(i) = 0
      }
      //Analizamos el juego para ver si termina o no
      if (nJuegos(0) == 3 || nJuegos(1) == 3) { //miramos si uno de los dos jugadores ha llegado a ganar 3 juegos
        //termina el juego
        termina = true
        var ganador = 0 //variable para almacenar al ganador
        if (nJuegos(1) == 3) { //si había ganado el jugador 1
          ganador = 1
        }
        println(s"Fin del concurso. Ha ganado el jugador $ganador")
      }
      juego += 1 //siguiente juego
      esperaOtroJugador.release() //liberamos al otro jugador que estaba bloqueado
      mutex.release()
    } else if (nTiradas(id) == 10){ //si el jugador ya ha terminado sus tiradas, se bloquea y espera al otro jugador
      mutex.release()
      esperaOtroJugador.acquire()
    } else{ //si no han terminado ninguno de los dos
      mutex.release()
    }
  }

  def concursoTerminado(): Boolean = {
    termina
  }
}
