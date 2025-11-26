# Damian Dice

Nos han solicitado a Diego Gil Lis y a Gregorio Camara Depablos el desarrollo de un proyecto: la creación de un juego idéntico a "Simón Dice", el cual estará personalizado para nuestro cliente, Damián.

Este proyecto se concibe como una aplicación interactiva que pondrá a prueba la memoria y la concentración del usuario, siguiendo la mecánica clásica del juego: replicar una secuencia de colores que se vuelve progresivamente más larga.

---
Preparación 
---

Para dar inicio al desarrollo de esta aplicación, la primera etapa crucial fue la conceptualización de su flujo interno. Como es tradición en desarrollo de software, lo primero que hicimos fue coger papel y lápiz. Este enfoque tradicional nos permitió esquematizar rápidamente las transiciones y estados del sistema en el siguiente desplegable.

<details><summary><h3>Diagrama de estados</h3></summary>
    
```mermaid
stateDiagram-v2
    Idle --> Opciones
    Idle --> Generando
    Generando --> Opciones
    Generando -->Adivinando
    Adivinando --> Adivinando
    Adivinando --> Opciones
    Adivinando --> Record/Puntuacion
    Record/Puntuacion -->Idle
    Opciones --> Idle
    Opciones --> Adivinando
    Opciones --> Generando

nota: actualizado a la ultima versión de Android Studio 2.1
```

</details>


---
Desarrollo del proyecto
---

Una vez definido el esqueleto lógico mediante el Diagrama de Estados, la siguiente prioridad fue la organización de las tareas y la implementación del entorno de trabajo.

Procedimos a dividirnos las responsabilidades mediante la creación de issues específicos en nuestro repositorio de GitHub. Esta metodología de trabajo no solo nos permite un seguimiento eficiente del progreso, sino que también evita conflictos de código. Después de haber comprendido y asignado claramente las funciones que debía cumplir cada miembro del equipo, comenzamos el desarrollo.

Utilizando las buenas prácticas y los patrones de diseño aprendidos en clase, dimos inicio a la codificación mediante la creación de las siguientes clases principales que modelarán la arquitectura de nuestro juego "Simón Dice":

<details><summary><h3>Datos</h3></summary>

Actúa como un contenedor de estado global y constantes para el juego. Almacena las variables clave que persisten a lo largo de la ejecución.

Elementos Clave:

**Datos (object):** Almacena el puntaje actual (victorias), el récord (rondasSuperadas) y la última secuencia generada (numero). Utiliza MutableStateFlow para permitir la observación reactiva del score por parte de la interfaz.

**Colores (enum):** Define los cuatro colores principales del juego (Rojo, Verde, Azul, Amarillo) y el color del botón Start, asociando a cada uno su respectivo objeto Color de Compose.

**Estados (enum):** Define los cinco estados posibles del juego (INICIO, GENERANDO, ADIVINANDO, RECORD, ERROR), controlando qué elementos de la interfaz deben estar activos (botones, Start) en cada momento.

</details>


<details><summary><h3>MyViewModel</h3></summary>

Es el cerebro de la aplicación. Contiene toda la lógica del juego, maneja los estados y coordina la actualización de los datos.

Elementos Clave:

**estadoLiveData:** Variable clave que notifica a la interfaz (UI) sobre el estado actual del juego, siguiendo el patrón Observer.

**crearRandom():** Cambia el estado a GENERANDO, genera un número aleatorio (0 a 3) que representa la nueva secuencia y lo almacena.

**comprobar(ordinal:** Ejecuta la lógica central de validación. Compara el botón pulsado por el usuario con el número guardado. Si acierta, incrementa victorias; si falla, guarda el récord en rondasSuperadas, resetea victorias y cambia el estado a ERROR (Game Over).

</details>

<details><summary><h3>IU</h3></summary>

Se encarga de la presentación visual y reacciona a los cambios de estado del ViewModel. Es la "Vista" dentro del patrón MVVM.

Elementos Clave:

**IU(miViewModel):** Función principal que utiliza un observer para cambiar dinácticamente la pantalla (JuegoScreen o GameOverScreen) según el valor de estadoLiveData.

**JuegoScreen / GameOverScreen:** Componentes que dibujan la interfaz principal del juego y la pantalla de fin de partida, mostrando la puntuación y el récord.

**Boton / Boton_Start:** Componentes reutilizables que controlan su propia activación consultando el estado del juego (boton_activo / start_activo) y comunican las acciones de pulsación al MyViewModel.

</details>

<details><summary><h3>MainActivity</h3></summary>

Es el punto de entrada de la aplicación Android. Se encarga de inicializar el entorno y crear la instancia del MyViewModel para pasárselo a la interfaz.

</details>

---
Decisiones clave de diseño
---

A continuación, se justifican las principales decisiones arquitectónicas y tecnológicas adoptadas:

<details><summary><h3>Gestión del Estado del Juego</h3></summary> 

**Decisión:** Uso del enum class Estados en conjunto con LiveData en el ViewModel para representar una Máquina de Estados Finitos (FSM).

**Justificación:** Garantiza que el juego se encuentre siempre en un estado válido y predecible (INICIO, GENERANDO, ADIVINANDO, ERROR). La interfaz solo puede reaccionar a las transiciones definidas, evitando comportamientos inconsistentes (por ejemplo, impidiendo la entrada del usuario durante la fase GENERANDO).

</details>


<details><summary><h3>Uso de Jetpack Compose</h3></summary> 

**Decisión:** Interfaz de usuario declarativa implementada completamente con Jetpack Compose.

**Justificación:** Permite un desarrollo de UI más rápido y reactivo. La interfaz (UI.kt) se reconstruye automáticamente solo en los componentes afectados cuando el estado de los datos (observados vía LiveData o StateFlow) cambia, optimizando el rendimiento.

</details>

<details><summary><h3>Persistencia de Datos Reactiva</h3></summary> 

**Decisión:** Utilización de MutableStateFlow (en Datos.kt) para variables críticas como victorias y rondasSuperadas.

**Justificación:** Aunque se usa LiveData para el estado principal, StateFlow ofrece una manera eficiente y asíncrona de exponer flujos de datos a la UI. Esto permite que los composables (como el texto de la puntuación) se actualicen de forma reactiva y con mayor rendimiento.

</details>

<details><summary><h3>Componentes Reutilizables</h3></summary>

**Decisión:** Creación de funciones @Composable específicas como Boton y Boton_Start.

**Justificación:** Mejora la modularidad y reduce la duplicación de código. El componente Boton_Start incluye un LaunchedEffect para gestionar la animación de parpadeo de forma controlada y eficiente (solo se activa en el estado INICIO), separando la lógica visual de la lógica del juego.
</details>


---
Fuincionamiento del programa 
---

Esta sección presenta las pruebas de la aplicación "Damian Dice" que demuestran la correcta implementación de la lógica del juego y la gestión de los estados, confirmando el patrón MVVM

<p align="center">
  <img src="damiandice.gif" alt="Demostración del Juego" width="450"/>
</p>



  
