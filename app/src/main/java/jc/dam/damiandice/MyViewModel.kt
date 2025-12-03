package jc.dam.damiandice

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dam.pmdm.preferencias.ControllerPreference
import kotlin.collections.plusAssign

class MyViewModel(application: Application): AndroidViewModel(application) {

    // etiqueta para logcat
    private val TAG_LOG = "miDebug"

    // estados del juego
    // usamos LiveData para que la IU se actualice
    // patron de diseño observer
    var estadoLiveData: MutableLiveData<Estados> = MutableLiveData(Estados.INICIO)

    // este va a ser nuestra lista para la secuencia random
    // usamos mutable, ya que la queremos modificar
    var _numbers = mutableStateOf(0)



    // inicializamos variables cuando instanciamos
    init {
        // estado inicial
        Log.d(TAG_LOG, "Inicializamos ViewModel - Estado: ${estadoLiveData.value}")
        Datos.rondasSuperadas.value = ControllerPreference.obtenerRecord(application)
    }

    /**
     * crear entero random
     */
    fun crearRandom() {
        // cambiamos estado, por lo tanto la IU se actualiza

        Datos.secuenciaJugador.clear()
        Datos.isPrinted.value = false

        estadoLiveData.value = Estados.ESPERANDO
        _numbers.value = (0..3).random()
        Datos.secuenciaMaquina.add(_numbers.value)
        Log.d(TAG_LOG, "creamos random ${_numbers.value} - Estado: ${estadoLiveData.value}")
        Log.d(TAG_LOG, "Nueva secuencia: ${Datos.secuenciaMaquina}")

        estadoLiveData.value = Estados.GENERANDO
    }


    /**
     * comprobar si el boton pulsado es el correcto
     * @param ordinal: Int numero de boton pulsado
     * @return Boolean si coincide TRUE, si no FALSE
     */
    fun comprobar(ordinal: Int) {

        Datos.secuenciaJugador.add(ordinal)
        val index = Datos.secuenciaJugador.lastIndex

        Log.d(TAG_LOG, "comprobamos - Estado: ${estadoLiveData.value}")
        if (Datos.secuenciaJugador[index] != Datos.secuenciaMaquina[index]) {
            Log.d(TAG_LOG, "Fallo en la posición $index")

            Datos.derrotas.value ++
            Datos.rondasSuperadas.value = Datos.victorias.value
            Datos.victorias.value = 0
            //llamamos a esRecord cuando perdamos para verificar si se supero el record
            esRecord(Datos.rondasSuperadas.value)

            estadoLiveData.value = Estados.ERROR
            Log.d(TAG_LOG, "PERDIMOS - Estado: ${estadoLiveData.value}")

            return
        }

        if(Datos.secuenciaJugador.size < Datos.secuenciaMaquina.size){
            Log.d(TAG_LOG,"correcto pero faltan pulsos")
            return
        }

        Datos.victorias.value++

        Log.d(TAG_LOG, "Ronda completada. Victorias: ${Datos.victorias.value}")

        crearRandom()
    }
    // Función para que la pantalla de error pueda volver al inicio
    fun reiniciarJuego(){
        Log.d(TAG_LOG, "Reiniciando el juego.")

        Datos.secuenciaMaquina.clear()
        Datos.secuenciaJugador.clear()
        Datos.isPrinted.value = false

        estadoLiveData.value = Estados.INICIO
    }

    fun esRecord(posibleRecord: Int) {
        if (posibleRecord > obtenerRecord()) {
            ControllerPreference.actualizarRecord(getApplication(), posibleRecord)
            Datos.rondasSuperadas.value = posibleRecord
            Log.d("_PREF", "Es record")
        } else {
            Log.d("_PREF", "No es record")
        }
    }

    /**
     * Obtiene el record actual.
     * @return El record actual.
     */
    fun obtenerRecord(): Int {
        Datos.rondasSuperadas.value = ControllerPreference.obtenerRecord(getApplication())
        Log.d("_PREF", "Record: ${(Datos.rondasSuperadas.value)}")
        return Datos.rondasSuperadas.value
    }

}