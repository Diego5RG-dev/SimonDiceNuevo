package jc.dam.damiandice

import android.R
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import android.provider.CalendarContract.Colors
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color

@Composable
fun IU(miViewModel: MyViewModel) {
    var estadoActual by remember { mutableStateOf(miViewModel.estadoLiveData.value ?: Estados.INICIO) }

    // Observador del LiveData que actualiza el estado interno de Compose
    miViewModel.estadoLiveData.observe(LocalLifecycleOwner.current) { nuevoEstado ->
        estadoActual = nuevoEstado
    }

    // Elige la pantalla a mostrar basándose en el estado
    when (estadoActual) {
        Estados.INICIO, Estados.GENERANDO, Estados.ADIVINANDO, Estados.RECORD -> {
            JuegoScreen(miViewModel = miViewModel)
        }
        Estados.ERROR -> {
            GameOverScreen(miViewModel = miViewModel)
        }

        else -> {}
    }
}

@Composable
fun Boton(miViewModel: MyViewModel, enum_color: Colores, colorBoton: Color = enum_color.color)  {

    //para buscar la etiqueta log mas facil
    val TAG_LOG = "miDebug"

    //variable para rastrear el estado del boton
    var _activo by remember { mutableStateOf(miViewModel.estadoLiveData.value!!.boton_activo) }

    miViewModel.estadoLiveData.observe(LocalLifecycleOwner.current) {
        // Log.d(TAG_LOG, "Observer Estado: ${miViewModel.estadoLiveData.value!!.name}")
        _activo = miViewModel.estadoLiveData.value!!.boton_activo
    }

    //Separador entre los botones
    Spacer(modifier = Modifier.size(10.dp))

    Button(
        enabled = _activo,
        shape = RectangleShape,

        colors = ButtonDefaults.buttonColors(colorBoton),
        onClick = {
            Log.d(TAG_LOG, "Dentro del boton: ${enum_color.id}")
            miViewModel.comprobar(enum_color.id)
        },

        modifier = Modifier
            .size(120.dp,120.dp)
            .padding(all = 8.dp)
    ) {
        Text(text = "")
    }
}

@Composable
fun JuegoScreen(miViewModel: MyViewModel) {
// Observamos las victorias para la ronda actual
    val victorias by Datos.victorias.collectAsState()
    // Observamos el record guardado
    val rondasSuperadas by Datos.rondasSuperadas.collectAsState()

    //guardamos los botones en variables que observamos constantemente
    val context = LocalContext.current
    val redButtonColor = remember { mutableStateOf(Colores.CLASE_ROJO.color) }
    val blueButtonColor = remember { mutableStateOf(Colores.CLASE_AZUL.color) }
    val greenButtonColor = remember { mutableStateOf(Colores.CLASE_VERDE.color) }
    val yellowButtonColor = remember { mutableStateOf(Colores.CLASE_AMARILLO.color) }
    val coroutineScope = rememberCoroutineScope()
    var _colorear by remember { mutableStateOf(miViewModel.estadoLiveData.value!!.colorearSecuencia) }

    suspend fun colorearSecuencia (){
        Datos.isPrinted.value = true
        for (i in Datos.secuenciaMaquina){
            delay(300)
            when(i){
                Colores.CLASE_ROJO.id -> {
                    redButtonColor.value = Colores.CLASE_ROJO.color_suave
                    delay(1000)
                    redButtonColor.value = Colores.CLASE_ROJO.color
                }
                Colores.CLASE_AZUL.id -> {
                    blueButtonColor.value = Colores.CLASE_AZUL.color_suave
                    delay(1000)
                    blueButtonColor.value = Colores.CLASE_AZUL.color
                }
                Colores.CLASE_VERDE.id -> {
                    greenButtonColor.value = Colores.CLASE_VERDE.color_suave
                    delay(1000)
                    greenButtonColor.value = Colores.CLASE_VERDE.color
                }
                Colores.CLASE_AMARILLO.id -> {
                    yellowButtonColor.value = Colores.CLASE_AMARILLO.color_suave
                    delay(1000)
                    yellowButtonColor.value = Colores.CLASE_AMARILLO.color
                }
            }
        }
        miViewModel.estadoLiveData.value = Estados.ADIVINANDO
    }

    miViewModel.estadoLiveData.observe(LocalLifecycleOwner.current) {
        _colorear = miViewModel.estadoLiveData.value!!.colorearSecuencia
        if (_colorear && !Datos.isPrinted.value){
            coroutineScope.launch {
                colorearSecuencia()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = "Ronda: $victorias  / Record: $rondasSuperadas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column {
            Row {
               //creamos el boton rojo
                Boton(miViewModel, Colores.CLASE_ROJO, redButtonColor.value)

                //creamos el boton verde
                Boton(miViewModel, Colores.CLASE_VERDE, greenButtonColor.value)
            }
            Row {
                //creamos el boton azul
                Boton(miViewModel, Colores.CLASE_AZUL, blueButtonColor.value)

                //creamos el boton amarillo
                Boton(miViewModel, Colores.CLASE_AMARILLO, yellowButtonColor.value)
            }
        }
        //Aqui colocamos el boton Start
        Boton_Start(miViewModel, Colores.CLASE_START)
    }
}

@Composable
fun GameOverScreen(miViewModel: MyViewModel) {
    // Observamos el puntaje final
    val puntaje by Datos.rondasSuperadas.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡FALLO!",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Colores.CLASE_ROJO.color
        )
        Spacer(modifier = Modifier.size(32.dp))

        Text(
            text = "Rondas Superadas:",
            fontSize = 28.sp
        )

        Text(
            text = "$puntaje",
            fontSize = 64.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.size(48.dp))

        Button(
            onClick = {
                // Llama a la función del ViewModel para volver a INICIO
                miViewModel.reiniciarJuego()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Colores.CLASE_VERDE.color),
            modifier = Modifier.size(200.dp, 60.dp)
        ) {
            Text(text = "Volver a Empezar", fontSize = 16.sp)
        }
    }
}

    @Composable
    fun Boton_Start(miViewModel: MyViewModel, enum_color: Colores){

        //Etiqueta facil de log
        val TAG_LOG = "miDebug"

        //Variable para el estado del boton
        var _activo by remember { mutableStateOf(miViewModel.estadoLiveData.value!!.start_activo) }

        //variable para el color utilizado en el LaunchedEffect
        var _color by remember { mutableStateOf(enum_color.color) }

        miViewModel.estadoLiveData.observe(LocalLifecycleOwner.current) {
            // Log.d(TAG_LOG, "Observer Estado: ${miViewModel.estadoLiveData.value!!.name}")
            _activo = miViewModel.estadoLiveData.value!!.start_activo
        }

        // cremos el efecto de parpadear con Launchedffect
        // mientras el estado es INICIO el boton start parpadea
        // si cambia _activo, el LaunchedEffect se inicia o se para

        LaunchedEffect(_activo) {
            Log.d(TAG_LOG, "LaunchedEffect - Estado: ${_activo}")
            //solo entra aqui si el boton esta activo = true
            while(_activo){
                _color = enum_color.color_suave
                delay(675)
                _color = enum_color.color
                delay(370)
            }
        }

        //separador entre botones
        Spacer(modifier = Modifier.size(40.dp))
        Button(
            enabled = _activo,
            //utilizamos el color del enum
            colors = ButtonDefaults.buttonColors(_color),
            onClick = {
                Log.d(TAG_LOG, "Dentro del Start - Estado: ${miViewModel.estadoLiveData.value!!.name}")
                miViewModel.crearRandom()
            },
            modifier = Modifier
                .size(100.dp,40.dp)
        ) {
            //Utilizamos el texto del enum_color
            Text(text = enum_color.txt, fontSize = 10.sp)
        }
    }


/*@Preview(showBackground = true)
@Composable
fun IUPreview(){
    IU(MyViewModel())
}*/