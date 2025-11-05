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



  
