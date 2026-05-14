# Ooodle-2026-1

Réplica del juego [Ooodle](https://mathszone.co.uk/resources/grid/ooodle/) desarrollada en Java y JavaFX con arquitectura MVC. Ooodle es un puzzle matemático estilo Wordle: el jugador tiene hasta 6 intentos para descubrir en qué posición van los números dentro de una ecuación, usando cada dígito del 1 al 9 una sola vez. Al verificar cada intento, las celdas se colorean en verde si el número está en la posición correcta, amarillo si está en la ecuación pero en otra posición, y gris si no pertenece a la ecuación.

## Requisitos previos

Para ejecutar el proyecto se necesita tener instalado **Java 21** (JDK o JRE) y el **JavaFX 21 SDK**. JavaFX no viene incluido en Java desde la versión 11 en adelante, por lo que debe descargarse por separado desde [gluonhq.com/products/javafx](https://gluonhq.com/products/javafx/).

## Ejecución

El proyecto puede ejecutarse de dos formas. La más sencilla es correr directamente el archivo `Ooodle-2026-1.jar` ubicado en la raíz del repositorio. La otra opción es abrir el proyecto como proyecto Maven en un IDE (IntelliJ IDEA, Eclipse, VS Code, etc.), navegar hasta `src/Vista/` y ejecutar `App.java` desde ahí.
