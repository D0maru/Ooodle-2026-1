package ftgw.ooodle.Modelo;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class Juego {

    // ===== CONFIGURACIÓN =====
    private final boolean modoDificil;

    // ===== VARIABLES DEL JUEGO =====
    private int target;
    private int intentoActual = 1;
    private int columnaActual = 0;
    private int[] solucion;

    private TextField[][] tablero;
    private Label[] resultados;

    // ===== COLORES =====
    private static final String VERDE    = "-fx-background-color: #00e676; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;";
    private static final String AMARILLO = "-fx-background-color: #ffd600; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;";
    private static final String GRIS     = "-fx-background-color: #616161; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 16px;";

    // ===== CONSTRUCTOR =====
    public Juego(boolean modoDificil, TextField[][] tablero, Label[] resultados) {
        this.modoDificil = modoDificil;
        this.tablero     = tablero;
        this.resultados  = resultados;
    }

    // ===== GENERAR NUEVO JUEGO =====
    public void GenerarNuevoJuego() {
        target = modoDificil
            ? (int)(Math.random() * 149) - 7
            : (int)(Math.random() * 83)  - 4;

        solucion = Ecuacion.GenerarEcuacion(target, modoDificil);

        if (solucion != null) {
            System.out.printf("SOLUCIÓN: %d * %d + %d - %d = %d%n",
                solucion[0], solucion[1], solucion[2], solucion[3], target);
        } else {
            System.out.println("No se encontró solución para: " + target);
        }

        for (Label l : resultados) {
            l.setText(String.valueOf(target));
        }
    }

    // ===== ESCRIBIR NÚMERO =====
    public void EscribirNumero(String num) {
        if (intentoActual > 6) return;

        TextField campo = tablero[intentoActual - 1][columnaActual];
        if (!campo.isEditable()) return;

        if (campo.getText().isEmpty()) {
            campo.setText(num);
            columnaActual++;
            if (columnaActual > 3) columnaActual = 3;
        }
    }

    // ===== BORRAR DÍGITO =====
    public void BorrarDigito() {
        if (intentoActual > 6) return;
        if (!tablero[intentoActual - 1][0].isEditable()) return;

        if (columnaActual > 0 && tablero[intentoActual - 1][columnaActual].getText().isEmpty()) {
            columnaActual--;
        }
        tablero[intentoActual - 1][columnaActual].clear();
    }

    // ===== MOSTRAR ERROR =====
    private void MostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ===== APLICAR COLORES =====
    private void AplicarColores(int fila, int[] intento) {
        for (int j = 0; j < 4; j++) {
            TextField celda = tablero[fila][j];

            if (intento[j] == solucion[j]) {
                celda.setStyle(VERDE);
            } else {
                boolean estaEnSolucion = false;
                for (int s = 0; s < 4; s++) {
                    if (intento[j] == solucion[s]) {
                        estaEnSolucion = true;
                        break;
                    }
                }
                celda.setStyle(estaEnSolucion ? AMARILLO : GRIS);
            }
        }
    }

    // ===== VALIDAR FILA =====
    // Retorna: "GANASTE", "PERDISTE", "CONTINUA", o null si hay error de validación
    public String ValidarFila() {
        try {
            String[] valores = new String[4];
            String regex = modoDificil ? "([1-9]|1[0-2])" : "[1-9]";
            String mensajeRango = modoDificil
                ? "Solo se permiten números del 1 al 12."
                : "Solo se permiten números del 1 al 9.";

            for (int i = 0; i < 4; i++) {
                valores[i] = tablero[intentoActual - 1][i].getText();

                if (valores[i] == null || valores[i].trim().isEmpty()) {
                    MostrarError(mensajeRango);MostrarError("Debes completar todos los espacios.");
                    return null;
                }
                if (!valores[i].matches(regex)) {
                    MostrarError(mensajeRango);
                    return null;
                }
            }

            int a = Integer.parseInt(valores[0]);
            int b = Integer.parseInt(valores[1]);
            int c = Integer.parseInt(valores[2]);
            int d = Integer.parseInt(valores[3]);

            if (a == b || a == c || a == d || b == c || b == d || c == d) {
                MostrarError("No puedes usar números repetidos.");
                return null;
            }

            AplicarColores(intentoActual - 1, new int[]{a, b, c, d});

            // Verificar victoria
            if (solucion != null &&
                a == solucion[0] && b == solucion[1] &&
                c == solucion[2] && d == solucion[3]) {
                return "GANASTE";
            }

            DeshabilitarFila(intentoActual - 1);
            intentoActual++;
            columnaActual = 0;

            if (intentoActual > 6) return "PERDISTE";

            HabilitarFila(intentoActual - 1);
            return "CONTINUA";

        } catch (Exception e) {
            e.printStackTrace();
            MostrarError("Ocurrió un error inesperado.");
            return null;
        }
    }

    // ===== REINICIAR =====
    public void ReiniciarJuego() {
        intentoActual = 1;
        columnaActual = 0;

        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 4; j++) {
                tablero[i][j].clear();
                tablero[i][j].setStyle("");
            }

        BloquearTodo();
        HabilitarFila(0);
        GenerarNuevoJuego();
    }

    // ===== GETTERS ÚTILES =====
    public int GetIntentoActual() { return intentoActual; }

    // ===== MANEJO DE FILAS =====
    public void BloquearTodo() {
        for (int i = 0; i < 6; i++) DeshabilitarFila(i);
    }

    public void HabilitarFila(int fila) {
        for (int j = 0; j < 4; j++) {
            tablero[fila][j].setEditable(true);
            tablero[fila][j].setDisable(false);
        }
    }

    public void DeshabilitarFila(int fila) {
        for (int j = 0; j < 4; j++) {
            tablero[fila][j].setEditable(false);
            tablero[fila][j].setDisable(true);
        }
    }
}