package dam.pmdm.preferencias

import android.content.Context
import androidx.core.content.edit
import jc.dam.damiandice.InterfazControlador

object ControllerPreference: InterfazControlador {
    // definimos el nombre del fichero de preferencias
    private const val PREFS_NAME = "preferencias_app"
    // definimos la clave del record (guardamos key:value)
    private const val KEY_RECORD = "record"

    /**
     * Actualiza el record en las preferencias compartidas.
     * @param context Contexto de la aplicación.
     * @param nuevoRecord Nuevo record a guardar.
     */

    override fun actualizarRecord(context: Context, nuevoRecord: Int) {
        // Obtenemos las preferencias compartidas
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Usamos la extensión KTX edit {} para no bloquear el hilo y aplicar cambios
        // 'put' pone un valor con clave KEY_RECORD y valor nuevoRecord
        sharedPreferences.edit {
            putInt(KEY_RECORD, nuevoRecord)
        }
    }

    /**
     * Obtiene el record de las preferencias compartidas.
     * @param context Contexto de la aplicación.
     * @return El valor del record, o 0 si no se encuentra.
     */

    override fun obtenerRecord(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_RECORD, 0)
    }
}