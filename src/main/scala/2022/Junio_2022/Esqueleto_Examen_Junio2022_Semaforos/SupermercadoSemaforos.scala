package Supermercado

import java.util.concurrent.Semaphore

class SupermercadoSemaforos extends Supermercado {
  /*
  DESCRIPCIÓN DEL PROBLEMA:
  Los clientes entran al supermercado mientras que esté abierto (cierre = False)
  Después son atendidos por los cajeros (siempre hay uno permanente)
  En caso de que haya más de tres clientes esperando se crea un nuevo cajero para atender
    - Se crea un cajero por cada 3 clientes de más que haya
  Cuando se cierre el supermercado se siguen atendiendo clientes hasta que no queden más (nClientes -=1)

  VARIABLES:
    - Ya declaradas:
      - Cajero.numCajeros() //devuelve el número de cajeros que hay activos en un momento determinado
    - Nuevas:
      - private var nClientes //Recurso compartido: número de clientes esperando en la cola
      - private val mutex = new Semaphore(1) // Semaforo para controlar el acceso a la variable
      - private var cierre = false //Variable para ver cuando podemos cerrar el supermercado
        - True = en el método fin()
        - !True = en el método nuevoCliente() para que puedan entrar nuevos

  MÉTODOS:
    - fin() = cuando ya no se pueden admitir a nuevos clientes -> cambia cierre = true
    - nuevoCliente() = tiene que hacer un acquire del semáfotro e incrementar la variable nClientes +=1
      - nClientes <= 3 => solo atiende el semáforo permanente
      - nClientes >3 => se crea nClientes%3 -1 cajeros nuevos
    - permanenteAtiendeCliente()
      - devuelve:
        - true = no quedan clientes y/o el supermercado está cerrado
        - false = si no está dormido -> quedan clientes
      - Variables locales:
        - var esperando = false //inicialmente el supermercado está abierto por lo que tiene que estar a false

  NOTAS:
    - Los cajeros atienden a los clientes en cualquier orden
   */

  //VARIABLES CREADAS
  private var cierre = false //inicialmente no está cerrado
  private var nClientes = 0 //inicialmente no hay clientes
  private val mutex = new Semaphore(1) //inicialmente nadie intenta acceder a la variable -> semáforo abierto

  //VARIABLES INICIALES
  private val permanente = new Cajero(this, true) // Crea el primer cajero, el permanente
  permanente.start()

  @throws[InterruptedException]
  override def fin(): Unit = {
    cierre = true //cerramos el supermercado
    println(s"SUPERMERCADO CERRADO")
  }

  @throws[InterruptedException]
  override def nuevoCliente(id: Int): Unit = {
    if(!cierre){ //no pueden entrar nuevos clientes si el supermercado está cerrado
      mutex.acquire()
      nClientes += 1
      println(s"Llega cliente $id. Hay $nClientes clientes.")
      if (nClientes > 3 * Cajero.numCajerosActivos())
       //Creamos un nuevo cajero
       val ocasional = new Cajero(this, false) //Consultar constructor cajero para más información
        println(s"Se crea un cajero nuevo ${Cajero.numCajerosActivos()-1}")
        ocasional.start() //iniciamos la actividad del cajero
      mutex.release()
    }
  }
/*
  @throws[InterruptedException]
  override def permanenteAtiendeCliente(id: Int): Boolean = {
    var atiende = false
    mutex.acquire()
    if (!cierre){ //Supermercado abierto
      if (nClientes >0){ //Caso 1: Abierto + clientes -> atiende
        nClientes -= 1 //decrementa el número de clientes esperando
        atiende = true
        println(s"Cajero permanente atiende a un cliente. Quedan $nClientes clientes.")
        mutex.release()
      }else{ //Caso 2: Abierto + sin clientes -> espera
        atiende = false //no atiende
        println(s"Cajero permanente espera.")
        mutex.release()
      }
    } else{ //Supermercado cerrado
      if (nClientes >0){ //Caso 3: Cerrado + clientes
        nClientes -= 1 //decrementa el número de clientes esperando
        println(s"Cajero permanente atiende a un cliente. Quedan $nClientes clientes.")
        mutex.release()
      }else{ //Caso 4: Cerrado + sin clientes -> termina
          atiende = false
          println("Cajero permanente termina.")
          mutex.release()
      }
    }
    atiende
  }
*/

  @throws[InterruptedException]
  override def permanenteAtiendeCliente(id: Int): Boolean = {
    var atiende = false
    mutex.acquire()
    if (nClientes > 0){       //HAY CLIENTES
      nClientes -= 1
      atiende = true
      println(s"Cajero permanente atiende. Hay $nClientes clientes.")
      if (nClientes == 0) {
        if(cierre) //era el último cliente cuando estaba cerrado
          println("Cajero permanente termina")
        else //era el último cliente cuando estaba abierto
          println("Cajero espera")
        atiende = false
      }
      mutex.release()
    } else { //NO HAY CLIENTES
      atiende = false
      if (cierre) //no hay clientes y está cerrado
        println("Cajero permanente termina")
      else //no hay clientes y está abierto -> espera
        println("Cajero espera")
      mutex.release()
    }
    atiende
  }

  @throws[InterruptedException]
  override def ocasionalAtiendeCliente(id: Int): Boolean = {
    var atiende = false
    mutex.acquire() //accedemos al semáforo
    if (nClientes > 0) //si aún quedan clientes
      nClientes -= 1 //atendemos al cliente
      println(s"Cajero $id atiende a un cliente. Quedan $nClientes clientes. ")
      atiende = true
    else{
      atiende = false //no quedan clientes y entonces no atiende a nadie
    }
    mutex.release() //libera el semáforo
    return atiende
  }
}
