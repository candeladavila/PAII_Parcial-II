package Supermercado

import java.util.concurrent.Semaphore

class SupermercadoSemaforos extends Supermercado {

  private val permanente = new Cajero(this, true) // Crea el primer cajero, el permanente
  permanente.start()

  // TODO: aquí pueden declararse semáforos y estructuras de sincronización necesarias

  @throws[InterruptedException]
  override def fin(): Unit = {
    // TODO: implementación pendiente
  }

  @throws[InterruptedException]
  override def nuevoCliente(id: Int): Unit = {
    // TODO: implementación pendiente
  }

  @throws[InterruptedException]
  override def permanenteAtiendeCliente(id: Int): Boolean = {
    // TODO: implementación pendiente
    true // borrar cuando se implemente
  }

  @throws[InterruptedException]
  override def ocasionalAtiendeCliente(id: Int): Boolean = {
    // TODO: implementación pendiente
    true // borrar cuando se implemente
  }
}
