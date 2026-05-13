package ftgw.ooodle.Modelo;
 
/**
 * Modelo puro del juego Ooodle.
 * No tiene ninguna dependencia de JavaFX: trabaja exclusivamente con
 * tipos primitivos y String. Toda la logica de UI (TextField, Label,
 * Alert, estilos CSS) es responsabilidad de los controladores.
 */
public class Juego {
 
    // ── Claves semánticas de color devueltas al controlador ───────────────
    // Los estilos CSS concretos son responsabilidad de cada controlador.
    public static final String COLOR_VERDE    = "VERDE";
    public static final String COLOR_AMARILLO = "AMARILLO";
    public static final String COLOR_GRIS     = "GRIS";
 
    // ── Estado interno ────────────────────────────────────────────────────
    private final boolean modoDificil;
 
    private int    target;
    private int    intentoActual = 1;
    private int    columnaActual = 0;
    private int[]  solucion;
 
    /** Valores escritos por el jugador en cada celda (6 filas x 4 columnas). */
    private final String[][]  celdas   = new String[6][4];
 
    /** true = la celda esta habilitada para edicion. */
    private final boolean[][] editable = new boolean[6][4];
 
    private final Usuario  usuario;
    private final Ecuacion ecuacion;
 
    // ── Constructor ───────────────────────────────────────────────────────

    /**
     * @param modoDificil true = modo dificil (numeros 1-12), false = facil (1-9).
     * @param usuario     jugador activo.
     * @param ecuacion    instancia de Ecuacion ya creada por el controlador,
     *                    acorde a la dificultad que se este jugando.
     */
    public Juego(boolean modoDificil, Usuario usuario, Ecuacion ecuacion) {
        this.modoDificil = modoDificil;
        this.usuario     = usuario;
        this.ecuacion    = ecuacion;

        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 4; j++) {
                celdas[i][j]   = "";
                editable[i][j] = false;
            }
    }
 
    // ── API publica ───────────────────────────────────────────────────────
 
    public Usuario getUsuario() {
        return usuario;
    }
 
    /**
     * Genera una nueva ecuacion y devuelve el target para que el controlador
     * lo muestre en los Labels de resultado.
     */
    public int GenerarNuevoJuego() {
        int maxIntentos = 100;
        solucion = null;
 
        for (int i = 0; i < maxIntentos && solucion == null; i++) {
            target = modoDificil
                ? (int)(Math.random() * 149) - 7
                : (int)(Math.random() * 83)  - 4;
            solucion = ecuacion.GenerarEcuacion(target, modoDificil);
        }
 
        if (solucion == null) {
            target   = modoDificil ? 100 : 14;
            solucion = ecuacion.GenerarEcuacion(target, modoDificil);
        }
 
        return target;
    }
 
    /**
     * Escribe un numero en la primera celda vacia y editable de la fila actual.
     * Devuelve int[]{fila, columna} de la celda modificada, o null si no se pudo escribir.
     */
    public int[] EscribirNumero(String num) {
        if (intentoActual > 6) return null;
 
        for (int i = 0; i < 4; i++) {
            if (celdas[intentoActual - 1][i].isEmpty() && editable[intentoActual - 1][i]) {
                celdas[intentoActual - 1][i] = num;
                columnaActual = Math.min(i + 1, 3);
                return new int[]{intentoActual - 1, i};
            }
        }
        return null;
    }
 
    /**
     * Borra el digito en la columna actual de la fila activa.
     * Devuelve int[]{fila, columna} de la celda borrada, o null si no aplico.
     */
    public int[] BorrarDigito() {
        if (intentoActual > 6) return null;
        if (!editable[intentoActual - 1][0]) return null;
 
        if (columnaActual > 0 && celdas[intentoActual - 1][columnaActual].isEmpty()) {
            columnaActual--;
        }
        celdas[intentoActual - 1][columnaActual] = "";
        return new int[]{intentoActual - 1, columnaActual};
    }
 
    /**
     * Valida la fila actual y avanza el estado del juego.
     * Devuelve un ResultadoFila con el estado y los estilos a aplicar.
     */
    public ResultadoFila ValidarFila() {
        try {
            String regex        = modoDificil ? "([1-9]|1[0-2])" : "[1-9]";
            String mensajeRango = modoDificil
                ? "Solo se permiten numeros del 1 al 12."
                : "Solo se permiten numeros del 1 al 9.";
 
            String[] valores = celdas[intentoActual - 1].clone();
 
            for (int i = 0; i < 4; i++) {
                if (valores[i] == null || valores[i].trim().isEmpty()) {
                    return new ResultadoFila("Debes completar todos los espacios.");
                }
                if (!valores[i].matches(regex)) {
                    return new ResultadoFila(mensajeRango);
                }
            }
 
            int a = Integer.parseInt(valores[0]);
            int b = Integer.parseInt(valores[1]);
            int c = Integer.parseInt(valores[2]);
            int d = Integer.parseInt(valores[3]);
 
            if (a == b || a == c || a == d || b == c || b == d || c == d) {
                return new ResultadoFila("No puedes usar numeros repetidos.");
            }
 
            int filaValidada = intentoActual - 1;
            String[] estilos = calcularEstilos(new int[]{a, b, c, d});
 
            if (solucion != null &&
                a == solucion[0] && b == solucion[1] &&
                c == solucion[2] && d == solucion[3]) {
                return new ResultadoFila("GANASTE", estilos, filaValidada);
            }
 
            DeshabilitarFila(intentoActual - 1);
            intentoActual++;
            columnaActual = 0;
 
            if (intentoActual > 6) {
                return new ResultadoFila("PERDISTE", estilos, filaValidada);
            }
 
            HabilitarFila(intentoActual - 1);
            return new ResultadoFila("CONTINUA", estilos, filaValidada);
 
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultadoFila("Ocurrio un error inesperado.");
        }
    }
 
    /**
     * Reinicia el estado interno y genera una nueva ecuacion.
     * Devuelve el nuevo target para que el controlador actualice los Labels.
     */
    public int ReiniciarJuego() {
        intentoActual = 1;
        columnaActual = 0;
 
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 4; j++) {
                celdas[i][j]   = "";
                editable[i][j] = false;
            }
 
        HabilitarFila(0);
        return GenerarNuevoJuego();
    }
 
    public int GetIntentoActual() {
        return intentoActual;
    }
 
    public void BloquearTodo() {
        for (int i = 0; i < 6; i++) DeshabilitarFila(i);
    }
 
    public void HabilitarFila(int fila) {
        for (int j = 0; j < 4; j++) editable[fila][j] = true;
    }
 
    public void DeshabilitarFila(int fila) {
        for (int j = 0; j < 4; j++) editable[fila][j] = false;
    }
 
    /** Devuelve el valor actual de la celda [fila][col]. */
    public String getValorCelda(int fila, int col) {
        return celdas[fila][col];
    }
 
    /** Indica si la celda [fila][col] esta habilitada para edicion. */
    public boolean isEditable(int fila, int col) {
        return editable[fila][col];
    }
 
    // ── Logica interna ────────────────────────────────────────────────────

    private String[] calcularEstilos(int[] intento) {
        String[] estilos = new String[4];
        for (int j = 0; j < 4; j++) {
            if (intento[j] == solucion[j]) {
                estilos[j] = COLOR_VERDE;
            } else {
                boolean estaEnSolucion = false;
                for (int s = 0; s < 4; s++) {
                    if (intento[j] == solucion[s]) {
                        estaEnSolucion = true;
                        break;
                    }
                }
                estilos[j] = estaEnSolucion ? COLOR_AMARILLO : COLOR_GRIS;
            }
        }
        return estilos;
    }
}