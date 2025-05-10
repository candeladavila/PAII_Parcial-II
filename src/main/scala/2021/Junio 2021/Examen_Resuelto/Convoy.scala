class Convoy(tam: Int) {

  def unir(id: Int): Int = {
    // TODO: Poner los mensajes donde corresponda
    println(s"** Furgoneta $id lidera del convoy **")
    println(s"Furgoneta $id seguidora")
    0
  }

  def calcularRuta(id: Int): Unit = {
    // TODO
    println(s"** Furgoneta $id lider:  ruta calculada, nos ponemos en marcha **")
  }

  def destino(id: Int): Unit = {
    // TODO
    println(s"** Furgoneta $id lider abandona el convoy **")
  }

  def seguirLider(id: Int): Unit = {
    // TODO
    println(s"Furgoneta $id abandona el convoy")
  }
}
