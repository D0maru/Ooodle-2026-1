package ftgw.ooodle.Modelo;

import java.util.Arrays;

public class Juego {

    private final boolean modoDificil;
    private int target;
    private int intentoActual = 0; // Usamos base 0 para arrays: 0 a 5
    private int[] solucion;
    private Ecuacion ecuacion;
    private Usuario usuario;

    // Matriz de datos puros. -1 significa celda vacía.
    private int[][] tableroDatos = new int[6][4];

    /** 
     * @return int[]
     */
    public int[] getSolucion() {
        return solucion;
    }

    /** 
     * @param solucion
     * @return boolean
     */
    public boolean setSolucion(int[] solucion) {
        this.solucion = solucion;
        return this.solucion == solucion;
    }

    /** 
     * @return Ecuacion
     */
    public Ecuacion getEcuacion() {
        return ecuacion;
    }

    /** 
     * @param ecuacion
     * @return boolean
     */
    public boolean setEcuacion(Ecuacion ecuacion) {
        this.ecuacion = ecuacion;
        return this.ecuacion == ecuacion;
    }

    /** 
     * @return int[][]
     */
    public int[][] getTableroDatos() {
        return tableroDatos;
    }

    /** 
     * @param tableroDatos
     * @return boolean
     */
    public boolean setTableroDatos(int[][] tableroDatos) {
        this.tableroDatos = tableroDatos;
        return this.tableroDatos == tableroDatos;
    }

    public Juego(boolean modoDificil, Ecuacion ecuacion, Usuario usuario) {
        this.modoDificil = modoDificil;
        this.ecuacion = ecuacion;
        this.usuario = usuario;
        reiniciarMatriz();
    }

    public Usuario getUsuario() { return usuario; }

    /** 
     * @return boolean
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
     * @return int
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
     * @param columna
     * @param valor
     * @return boolean
     */
    // LÓGICA DE EDICIÓN: El modelo recibe coordenadas y valores
    
    public boolean setNumeroEnCelda(int columna, int valor) {
        if (intentoActual < 6 && columna >= 0 && columna < 4) {
            tableroDatos[intentoActual][columna] = valor;
            return tableroDatos[intentoActual][columna] == valor;
        }
        return false;
    }

    /** 
     * @param columna
     * @return boolean
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
     * @param fila
     * @return boolean
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
     * @param n
     * @return boolean
     */
    private boolean estaEnSolucion(int n) {
        for (int s : solucion) if (s == n) return true;
        return false;
    }

    /** 
     * @param esGanador(
     * @return int
     */
    // GETTERS PARA EL CONTROLADOR
    public int getTarget() { return target; }
    public int getIntentoActual() { return intentoActual; }
    /** 
     * @return boolean
     */
    public boolean esGanador() {
        if (intentoActual == 0) return false;
        return Arrays.equals(tableroDatos[intentoActual - 1], solucion);
    }
}