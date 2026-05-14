package ftgw.ooodle.Modelo;
import java.util.Arrays;
/**
 * Clase principal que representa una partida del juego.
 * Gestiona el tablero, la solución, los intentos del usuario
 * y la lógica de validación de cada fila ingresada.
 */
public class Juego {
    /** Indica si la partida se juega en modo difícil. */
    private final boolean modoDificil;
    /** Valor objetivo que la ecuación debe producir. */
    private int target;
    /** Índice de la fila actual en el tablero (0 a 5). */
    private int intentoActual = 0; 
    /** Arreglo con los cuatro valores que resuelven la ecuación objetivo. */
    private int[] solucion;
    /** Instancia encargada de generar y evaluar ecuaciones matemáticas. */
    private Ecuacion ecuacion;
    /** Usuario asociado a la partida actual. */
    private Usuario usuario;

    /** Matriz 6x4 con los valores ingresados por el usuario. -1 representa una celda vacía. */
    private int[][] tableroDatos = new int[6][4];

    /**
     * Obtiene la solución actual de la partida.
     * @return Arreglo con los cuatro valores de la solución.
     */
    public int[] getSolucion() {
        return solucion;
    }
    /**
     * Establece la solución de la partida.
     * @param solucion Arreglo con los cuatro valores solución.
     * @return true si la asignación fue exitosa.
     */
    public boolean setSolucion(int[] solucion) {
        this.solucion = solucion;
        return this.solucion == solucion;
    }
    /**
     * Obtiene la instancia de Ecuacion asociada al juego.
     * @return Objeto Ecuacion actual.
     */
    public Ecuacion getEcuacion() {
        return ecuacion;
    }
    /**
     * Establece la instancia de Ecuacion a usar en la partida.
     * @param ecuacion Objeto Ecuacion a asignar.
     * @return true si la asignación fue exitosa.
     */
    public boolean setEcuacion(Ecuacion ecuacion) {
        this.ecuacion = ecuacion;
        return this.ecuacion == ecuacion;
    }
    /**
     * Obtiene la matriz de datos del tablero.
     * @return Matriz 6x4 con los valores ingresados; -1 indica celda vacía.
     */
    public int[][] getTableroDatos() {
        return tableroDatos;
    }
    /**
     * Reemplaza la matriz del tablero con una nueva.
     * @param tableroDatos Nueva matriz 6x4 a asignar.
     * @return true si la asignación fue exitosa.
     */
    public boolean setTableroDatos(int[][] tableroDatos) {
        this.tableroDatos = tableroDatos;
        return this.tableroDatos == tableroDatos;
    }
    /**
     * Crea una nueva partida inicializando el tablero y los componentes necesarios.
     * @param modoDificil true para activar el modo difícil.
     * @param ecuacion Instancia de Ecuacion para generar la solución.
     * @param usuario Usuario que juega la partida.
     */
    public Juego(boolean modoDificil, Ecuacion ecuacion, Usuario usuario) {
        this.modoDificil = modoDificil;
        this.ecuacion = ecuacion;
        this.usuario = usuario;
        reiniciarMatriz();
    }
    /**
     * Obtiene el usuario asociado a la partida.
     * @return Objeto Usuario de la partida actual.
     */
    public Usuario getUsuario() { return usuario; }
    /**
     * Rellena todas las celdas del tablero con -1, indicando que están vacías.
     * @return true si todas las celdas quedaron correctamente en -1; false si alguna falló.
     */
    private boolean reiniciarMatriz() {
        for (int i = 0; i < 6; i++) {
            Arrays.fill(tableroDatos[i], -1);
        }
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < tableroDatos[i].length; j++) {
                if (tableroDatos[i][j] != -1) return false;
            }
        }
        return true;
    }
    /**
     * Reinicia la partida y genera un nuevo target con su solución correspondiente.
     * Intenta hasta 100 veces encontrar una ecuación válida; si no lo logra,
     * usa un valor de respaldo predefinido.
     * @return El valor objetivo (target) generado para la nueva partida.
     */
    public int generarNuevoJuego() {
        int maxIntentos = 100;
        solucion = null;
        intentoActual = 0;
        reiniciarMatriz();

        for (int i = 0; i < maxIntentos && solucion == null; i++) {
            target = modoDificil ? (int)(Math.random() * 149) - 7 : (int)(Math.random() * 83) - 4;
            solucion = ecuacion.GenerarEcuacion(target, modoDificil);
        }

        if (solucion == null) {
            target = modoDificil ? 100 : 14;
            solucion = ecuacion.GenerarEcuacion(target, modoDificil);
        }
        return target;
    }
    /**
     * Asigna un valor en una celda específica de la fila actual.
     * @param columna Índice de la columna (0 a 3).
     * @param valor Número a colocar en la celda.
     * @return true si el valor fue asignado correctamente; false si las coordenadas son inválidas.
     */
    public boolean setNumeroEnCelda(int columna, int valor) {
        if (intentoActual < 6 && columna >= 0 && columna < 4) {
            tableroDatos[intentoActual][columna] = valor;
            return tableroDatos[intentoActual][columna] == valor;
        }
        return false;
    }
    /**
     * Limpia una celda de la fila actual, dejándola en -1.
     * @param columna Índice de la columna a borrar (0 a 3).
     * @return true si la celda fue borrada correctamente; false si las coordenadas son inválidas.
     */
    public boolean borrarCelda(int columna) {
        if (intentoActual < 6 && columna >= 0 && columna < 4) {
            tableroDatos[intentoActual][columna] = -1;
            return tableroDatos[intentoActual][columna] == -1;
        }
        return false;
    }

    /**
     * Valida la fila actual y devuelve un array de estados.
     * @return Array de 4 enteros: 2 (Verde), 1 (Amarillo), 0 (Gris). 
     *         Null si la fila está incompleta o tiene errores de regla.
     */
    public int[] validarIntento() {
        int[] fila = tableroDatos[intentoActual];

        // 1. Validar que no haya vacíos
        for (int num : fila) if (num == -1) return null;

        // 2. Validar que no haya repetidos
        if (tieneRepetidos(fila)) return null;

        // 3. Comparar con la solución
        int[] resultadoColores = new int[4]; 
        for (int i = 0; i < 4; i++) {
            if (fila[i] == solucion[i]) {
                resultadoColores[i] = 2; // Representa VERDE
            } else if (estaEnSolucion(fila[i])) {
                resultadoColores[i] = 1; // Representa AMARILLO
            } else {
                resultadoColores[i] = 0; // Representa GRIS
            }
        }

        intentoActual++; // Avanzamos de fila solo si la validación fue exitosa
        return resultadoColores;
    }
    /**
     * Verifica si una fila contiene valores duplicados.
     * @param fila Arreglo de cuatro enteros a evaluar.
     * @return true si hay al menos un valor repetido; false si todos son distintos.
     */
    private boolean tieneRepetidos(int[] fila) {
        for (int i = 0; i < fila.length; i++) {
            for (int j = i + 1; j < fila.length; j++) {
                if (fila[i] == fila[j]) return true;
            }
        }
        return false;
    }
    /**
     * Comprueba si un número está presente en la solución de la partida.
     * @param n Número a buscar.
     * @return true si el número existe en la solución; false en caso contrario.
     */
    private boolean estaEnSolucion(int n) {
        for (int s : solucion) if (s == n) return true;
        return false;
    }
    /**
     * Obtiene el valor objetivo de la partida actual.
     * @return Entero que representa el target.
     */
    public int getTarget() { return target; }
    /**
     * Obtiene el índice de la fila en la que se encuentra el jugador.
     * @return Número de intento actual (0 a 5).
     */
    public int getIntentoActual() { return intentoActual; }
    /**
     * Determina si el jugador ganó la partida.
     * @return true si el último intento coincide exactamente con la solución; false en caso contrario.
     */
    public boolean esGanador() {
        if (intentoActual == 0) return false;
        return Arrays.equals(tableroDatos[intentoActual - 1], solucion);
    }
}