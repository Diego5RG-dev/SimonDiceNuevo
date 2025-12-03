package jc.dam.damiandice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jc.dam.damiandice.ui.theme.DamianDiceTheme
import android.provider.Contacts.Intents.UI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Inicializamos el ViewModel
        val miViewModel= MyViewModel(application)

        enableEdgeToEdge()
        setContent {
            IU(miViewModel)
        }
    }
}

