package jc.dam.damiandice

import android.content.Context

interface InterfazControlador {
    fun actualizarRecord(context: Context, nuevoRecord: Int)
    fun obtenerRecord(context: Context): Int
}