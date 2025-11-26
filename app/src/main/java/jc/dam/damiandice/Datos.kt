package jc.dam.damiandice

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Clase para almacenar los datos del juego
 */
object Datos {

    var numero = 0
    val victorias = MutableStateFlow(0)
    var derrotas = MutableStateFlow(0)

    val rondasSuperadas = MutableStateFlow(0)

}


/**
 * Colores utilizados
 */
enum class Colores (val color: Color, val color_suave: Color = Color.Transparent , val txt: String){
    CLASE_ROJO(color = Color.Companion.Red, txt = ""),
    CLASE_VERDE(color = Color.Companion.Green, txt = ""),
    CLASE_AZUL(color = Color.Companion.Blue, txt = ""),
    CLASE_AMARILLO(color = Color.Companion.Yellow, txt = ""),
    CLASE_START(color = Color.Transparent, color_suave = Color.Red, txt = "Start")

}


/**
 * Estados del juego
 */
enum class Estados(val start_activo: Boolean, val boton_activo: Boolean) {
    INICIO(start_activo = true, boton_activo = false),
    GENERANDO(start_activo = false, boton_activo = false),
    ADIVINANDO(start_activo = false, boton_activo = true),

    RECORD(start_activo = false, boton_activo = true),
    ERROR (start_activo = false, boton_activo = false)

}