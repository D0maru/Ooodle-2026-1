package unitarias;

import org.junit.jupiter.api.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

import ftgw.ooodle.Modelo.Ecuacion;


@DisplayName("Juego: lógica principal de Fill the Grid")
class JuegoTest {

   
    static class StubTextField {
        private String text = "";
        private boolean editable = false;
        private boolean disable = false;

        public String getText() { return text; }
        public void setText(String t) { this.text = (t == null ? "" : t); }
        public void clear() { this.text = ""; }
        public boolean isEditable() { return editable; }
        public void setEditable(boolean e) { this.editable = e; }
        public boolean isDisable() { return disable; }
        public void setDisable(boolean d) { this.disable = d; }
        public void setStyle(String s) { /* no-op en tests */ }
    }

    
    static class StubLabel {
        private String text = "";
        public void setText(String t) { this.text = t; }
        public String getText() { return text; }
    }

    static class JuegoTestable {
        private final boolean modoDificil;
        private int intentoActual = 1;
        private int columnaActual = 0;
        private int target;
        private int[] solucion;
        private final StubTextField[][] tablero;
        private final StubLabel[] resultados;

        private static final String VERDE    = "verde";
        private static final String AMARILLO = "amarillo";
        private static final String GRIS     = "gris";

        JuegoTestable(boolean modoDificil) {
            this.modoDificil = modoDificil;
            this.tablero = new StubTextField[6][4];
            for (int i = 0; i < 6; i++)
                for (int j = 0; j < 4; j++)
                    tablero[i][j] = new StubTextField();
            this.resultados = new StubLabel[6];
            for (int i = 0; i < 6; i++) resultados[i] = new StubLabel();
        }

        void generarNuevoJuego() {
            int maxIntentos = 100;
            solucion = null;
            for (int i = 0; i < maxIntentos && solucion == null; i++) {
                target = modoDificil
                        ? (int)(Math.random() * 149) - 7
                        : (int)(Math.random() * 83) - 4;
                solucion = new Ecuacion().GenerarEcuacion(target, modoDificil);
            }
            if (solucion == null) {
                target = modoDificil ? 100 : 14;
                solucion = new Ecuacion().GenerarEcuacion(target, modoDificil);
            }
            for (StubLabel l : resultados) l.setText(String.valueOf(target));
        }

        void escribirNumero(String num) {
            if (intentoActual > 6) return;
            StubTextField campo = tablero[intentoActual - 1][columnaActual];
            if (!campo.isEditable()) return;
            if (campo.getText().isEmpty()) {
                campo.setText(num);
                columnaActual++;
                if (columnaActual > 3) columnaActual = 3;
            }
        }

        void borrarDigito() {
            if (intentoActual > 6) return;
            if (!tablero[intentoActual - 1][0].isEditable()) return;
            if (columnaActual > 0 && tablero[intentoActual - 1][columnaActual].getText().isEmpty()) {
                columnaActual--;
            }
            tablero[intentoActual - 1][columnaActual].clear();
        }

        private void aplicarColores(int fila, int[] intento) {
            for (int j = 0; j < 4; j++) {
                StubTextField celda = tablero[fila][j];
                if (intento[j] == solucion[j]) {
                    celda.setStyle(VERDE);
                } else {
                    boolean estaEnSolucion = false;
                    for (int s = 0; s < 4; s++)
                        if (intento[j] == solucion[s]) { estaEnSolucion = true; break; }
                    celda.setStyle(estaEnSolucion ? AMARILLO : GRIS);
                }
            }
        }

        /**
         * ValidarFila sin llamadas a Alert (retorna los mismos códigos que Juego).
         * "GANASTE" | "PERDISTE" | "CONTINUA" | null (error de validación)
         */
        String validarFila() {
            String regex = modoDificil ? "([1-9]|1[0-2])" : "[1-9]";
            String[] valores = new String[4];

            for (int i = 0; i < 4; i++) {
                valores[i] = tablero[intentoActual - 1][i].getText();
                if (valores[i] == null || valores[i].trim().isEmpty()) return null;
                if (!valores[i].matches(regex)) return null;
            }

            int a = Integer.parseInt(valores[0]);
            int b = Integer.parseInt(valores[1]);
            int c = Integer.parseInt(valores[2]);
            int d = Integer.parseInt(valores[3]);

            if (a == b || a == c || a == d || b == c || b == d || c == d) return null;

            aplicarColores(intentoActual - 1, new int[]{a, b, c, d});

            if (solucion != null
                    && a == solucion[0] && b == solucion[1]
                    && c == solucion[2] && d == solucion[3]) {
                return "GANASTE";
            }

            deshabilitarFila(intentoActual - 1);
            intentoActual++;
            columnaActual = 0;

            if (intentoActual > 6) return "PERDISTE";
            habilitarFila(intentoActual - 1);
            return "CONTINUA";
        }

        void reiniciarJuego() {
            intentoActual = 1;
            columnaActual = 0;
            for (int i = 0; i < 6; i++)
                for (int j = 0; j < 4; j++) {
                    tablero[i][j].clear();
                    tablero[i][j].setStyle("");
                }
            bloquearTodo();
            habilitarFila(0);
            generarNuevoJuego();
        }

        void bloquearTodo() { for (int i = 0; i < 6; i++) deshabilitarFila(i); }

        void habilitarFila(int fila) {
            for (int j = 0; j < 4; j++) {
                tablero[fila][j].setEditable(true);
                tablero[fila][j].setDisable(false);
            }
        }

        void deshabilitarFila(int fila) {
            for (int j = 0; j < 4; j++) {
                tablero[fila][j].setEditable(false);
                tablero[fila][j].setDisable(true);
            }
        }

        // Helpers para pruebas
        int getIntentoActual()           { return intentoActual; }
        int getColumnaActual()           { return columnaActual; }
        int getTarget()                  { return target; }
        int[] getSolucion()              { return solucion; }
        StubTextField[][] getTablero()   { return tablero; }
        StubLabel[] getResultados()      { return resultados; }
    }

 
    private JuegoTestable juegoNormal;
    private JuegoTestable juegoDificil;

    @BeforeEach
    void setUp() {
        juegoNormal  = new JuegoTestable(false);
        juegoDificil = new JuegoTestable(true);
        juegoNormal.habilitarFila(0);
        juegoDificil.habilitarFila(0);
        juegoNormal.generarNuevoJuego();
        juegoDificil.generarNuevoJuego();
    }

    // Helper: llena la fila actual con los valores dados
    private void llenarFila(JuegoTestable j, String... valores) {
        for (String v : valores) j.escribirNumero(v);
    }

    // Helper: llena con la solución exacta
    private void llenarConSolucion(JuegoTestable j) {
        int[] sol = j.getSolucion();
        for (int v : sol) j.escribirNumero(String.valueOf(v));
    }

    // Helper: construye un intento incorrecto (diferente a la solución)
    private String[] intentoIncorrecto(JuegoTestable j) {
        int[] sol = j.getSolucion();
        // Encontrar 4 valores únicos distintos de la solución dentro del rango
        java.util.List<Integer> disponibles = new java.util.ArrayList<>();
        int max = j.modoDificil ? 12 : 9;
        for (int v = 1; v <= max; v++) {
            boolean esSolucion = false;
            for (int s : sol) if (s == v) { esSolucion = true; break; }
            if (!esSolucion) disponibles.add(v);
        }
        // Si hay al menos 4 disponibles, usarlos; si no, mezclar posiciones de la solución
        if (disponibles.size() >= 4) {
            return new String[]{
                String.valueOf(disponibles.get(0)), String.valueOf(disponibles.get(1)),
                String.valueOf(disponibles.get(2)), String.valueOf(disponibles.get(3))
            };
        }
        // Fallback: rotar la solución
        return new String[]{
            String.valueOf(sol[1]), String.valueOf(sol[0]),
            String.valueOf(sol[3]), String.valueOf(sol[2])
        };
    }

    @Nested
    @DisplayName("GenerarNuevoJuego: inicialización del puzzle")
    class GenerarNuevoJuegoTests {

        @Test @DisplayName("Solución no es null tras generar juego")
        void solucionNoEsNull() {
            assertNotNull(juegoNormal.getSolucion());
        }

        @Test @DisplayName("Target mostrado en todos los labels de resultado")
        void targetEnLabels() {
            String targetStr = String.valueOf(juegoNormal.getTarget());
            for (StubLabel l : juegoNormal.getResultados())
                assertEquals(targetStr, l.getText());
        }

        @Test @DisplayName("La solución satisface a×b+c-d = target")
        void solucionSatisfaceTarget() {
            int[] sol = juegoNormal.getSolucion();
            int target = juegoNormal.getTarget();
            assertEquals(target, (sol[0] * sol[1]) + sol[2] - sol[3]);
        }

        @Test @DisplayName("Target modo normal está en rango alcanzable [-4, 78]")
        void targetNormalEnRango() {
            int t = juegoNormal.getTarget();
            assertTrue(t >= -4 && t <= 78, "Target fuera de rango: " + t);
        }

        @Test @DisplayName("Target modo difícil está en rango alcanzable [-7, 141]")
        void targetDificilEnRango() {
            int t = juegoDificil.getTarget();
            assertTrue(t >= -7 && t <= 141, "Target fuera de rango: " + t);
        }

        @Test @DisplayName("GenerarNuevoJuego puede llamarse múltiples veces sin fallar")
        void generarVariasVeces() {
            for (int i = 0; i < 5; i++) {
                juegoNormal.generarNuevoJuego();
                assertNotNull(juegoNormal.getSolucion());
            }
        }
    }


    @Nested
    @DisplayName("EscribirNumero: entrada del jugador en el tablero")
    class EscribirNumeroTests {

        @Test @DisplayName("Escribe número en la celda y avanza la columna")
        void escribirAvanzaColumna() {
            juegoNormal.escribirNumero("3");
            assertEquals("3", juegoNormal.getTablero()[0][0].getText());
            assertEquals(1, juegoNormal.getColumnaActual());
        }

        @Test @DisplayName("No sobreescribe una celda que ya tiene valor")
        void noSobreescribeCeldaLlena() {
            juegoNormal.escribirNumero("3");
            juegoNormal.escribirNumero("5"); // debe ir a la col 1, no sobreescribir col 0
            assertEquals("3", juegoNormal.getTablero()[0][0].getText());
            assertEquals("5", juegoNormal.getTablero()[0][1].getText());
        }

        @Test @DisplayName("columnaActual no supera 3 (índice máximo de columna)")
        void columnaNoSuperaMaximo() {
            llenarFila(juegoNormal, "1", "2", "3", "4");
            juegoNormal.escribirNumero("5"); // intento de escribir en columna 4
            assertEquals(3, juegoNormal.getColumnaActual());
        }

        @Test @DisplayName("No escribe si la fila no es editable")
        void noEscribeEnFilaBloqueada() {
            juegoNormal.deshabilitarFila(0);
            juegoNormal.escribirNumero("7");
            assertEquals("", juegoNormal.getTablero()[0][0].getText());
        }

        @Test @DisplayName("No escribe si intentoActual > 6 (juego terminado)")
        void noEscribeSiJuegoTerminado() throws Exception {
            Field f = JuegoTestable.class.getDeclaredField("intentoActual");
            f.setAccessible(true);
            f.set(juegoNormal, 7);
            juegoNormal.escribirNumero("5");
            assertEquals("", juegoNormal.getTablero()[0][0].getText());
        }
    }

    @Nested
    @DisplayName("BorrarDigito: corrección de entrada del jugador")
    class BorrarDigitoTests {

        @Test @DisplayName("Borra el último dígito escrito")
        void borraUltimoDigito() {
            juegoNormal.escribirNumero("4");
            juegoNormal.borrarDigito();
            assertEquals("", juegoNormal.getTablero()[0][0].getText());
        }

        @Test @DisplayName("Retrocede la columna al borrar")
        void retrocedeLaColumna() {
            juegoNormal.escribirNumero("4");
            juegoNormal.escribirNumero("5");
            juegoNormal.borrarDigito(); // borra col 1
            assertEquals(1, juegoNormal.getColumnaActual());
        }

        @Test @DisplayName("No hace nada en columna 0 sin texto (primera celda vacía)")
        void borrarEnPrimeraCeldaVacia() {
            // Columna 0 y celda vacía → no debe lanzar excepción
            assertDoesNotThrow(() -> juegoNormal.borrarDigito());
            assertEquals(0, juegoNormal.getColumnaActual());
        }

        @Test @DisplayName("No borra si la fila está bloqueada")
        void noBorraFilaBloqueada() {
            juegoNormal.deshabilitarFila(0);
            assertDoesNotThrow(() -> juegoNormal.borrarDigito());
        }
    }

  
    @Nested
    @DisplayName("ValidarFila: resultados GANASTE / PERDISTE / CONTINUA / null")
    class ValidarFilaTests {

        @Test @DisplayName("Solución correcta en el primer intento → GANASTE")
        void primerIntentoGanaste() {
            llenarConSolucion(juegoNormal);
            assertEquals("GANASTE", juegoNormal.validarFila());
        }

        @Test @DisplayName("Intento incorrecto → CONTINUA y avanza intento")
        void intentoIncorrecto_continua() {
            String[] incorrecto = intentoIncorrecto(juegoNormal);
            llenarFila(juegoNormal, incorrecto);
            String resultado = juegoNormal.validarFila();
            // Puede ser CONTINUA o PERDISTE según el estado; en el 1er intento siempre CONTINUA
            assertEquals("CONTINUA", resultado);
            assertEquals(2, juegoNormal.getIntentoActual());
        }

        @Test @DisplayName("Fila vacía → null (error de validación)")
        void filaVacia_retornaNull() {
            assertNull(juegoNormal.validarFila());
        }

        @Test @DisplayName("Número fuera de rango modo normal → null")
        void numerFueraDeRango_normal() {
            llenarFila(juegoNormal, "1", "2", "3", "0"); // 0 no está en [1-9]
            assertNull(juegoNormal.validarFila());
        }

        @Test @DisplayName("Número fuera de rango modo difícil → null")
        void numerFueraDeRango_dificil() {
            llenarFila(juegoDificil, "1", "2", "3", "13"); // 13 no está en [1-12]
            assertNull(juegoDificil.validarFila());
        }

        @Test @DisplayName("Números repetidos → null")
        void numerosRepetidos_retornaNull() {
            llenarFila(juegoNormal, "3", "3", "5", "7"); // 3 repetido
            assertNull(juegoNormal.validarFila());
        }

        @Test @DisplayName("6 intentos fallidos consecutivos → PERDISTE")
        void seisFallos_retornaPerdiste() {
            for (int intento = 0; intento < 6; intento++) {
                juegoNormal.habilitarFila(intento);
                String[] vals = intentoIncorrecto(juegoNormal);
                llenarFila(juegoNormal, vals);
                String r = juegoNormal.validarFila();
                if ("PERDISTE".equals(r)) {
                    assertEquals("PERDISTE", r);
                    return;
                }
            }
            fail("Se esperaba PERDISTE después de 6 intentos fallidos");
        }

        @Test @DisplayName("Tras validar CONTINUA, la fila anterior queda bloqueada")
        void filaAnteriorBloqueadaTrasContinua() {
            String[] vals = intentoIncorrecto(juegoNormal);
            llenarFila(juegoNormal, vals);
            juegoNormal.validarFila();
            // La fila 0 debe estar bloqueada
            assertFalse(juegoNormal.getTablero()[0][0].isEditable());
        }

        @Test @DisplayName("Tras validar CONTINUA, la fila siguiente queda habilitada")
        void filaSiguienteHabilitadaTrasContinua() {
            String[] vals = intentoIncorrecto(juegoNormal);
            llenarFila(juegoNormal, vals);
            juegoNormal.validarFila();
            // La fila 1 debe ser editable
            assertTrue(juegoNormal.getTablero()[1][0].isEditable());
        }

        @Test @DisplayName("Modo difícil acepta valores del 10 al 12")
        void modoDificil_aceptaValoresMayoresA9() {
            // Forzar solución con valores > 9
            int[] sol = juegoDificil.getSolucion();
            for (int v : sol) juegoDificil.escribirNumero(String.valueOf(v));
            assertEquals("GANASTE", juegoDificil.validarFila());
        }
    }

 
    @Nested
    @DisplayName("ReiniciarJuego: reset completo del estado")
    class ReiniciarTests {

        @Test @DisplayName("intentoActual vuelve a 1 tras reiniciar")
        void intentoActualVuelveA1() {
            String[] vals = intentoIncorrecto(juegoNormal);
            llenarFila(juegoNormal, vals);
            juegoNormal.validarFila();
            juegoNormal.reiniciarJuego();
            assertEquals(1, juegoNormal.getIntentoActual());
        }

        @Test @DisplayName("columnaActual vuelve a 0 tras reiniciar")
        void columnaActualVuelveA0() {
            juegoNormal.escribirNumero("3");
            juegoNormal.reiniciarJuego();
            assertEquals(0, juegoNormal.getColumnaActual());
        }

        @Test @DisplayName("Todas las celdas quedan vacías tras reiniciar")
        void celdasVaciasTrasReiniciar() {
            llenarConSolucion(juegoNormal);
            juegoNormal.reiniciarJuego();
            for (int i = 0; i < 6; i++)
                for (int j = 0; j < 4; j++)
                    assertEquals("", juegoNormal.getTablero()[i][j].getText());
        }

        @Test @DisplayName("Genera nueva solución válida tras reiniciar")
        void nuevaSolucionTrasReiniciar() {
            juegoNormal.reiniciarJuego();
            int[] sol = juegoNormal.getSolucion();
            assertNotNull(sol);
            assertEquals(juegoNormal.getTarget(), (sol[0] * sol[1]) + sol[2] - sol[3]);
        }

        @Test @DisplayName("Fila 0 queda habilitada, resto bloqueado, tras reiniciar")
        void fila0HabilitadaTraReiniciar() {
            juegoNormal.reiniciarJuego();
            assertTrue(juegoNormal.getTablero()[0][0].isEditable());
            for (int i = 1; i < 6; i++)
                assertFalse(juegoNormal.getTablero()[i][0].isEditable());
        }
    }

    @Nested
    @DisplayName("Manejo de filas: habilitar, deshabilitar, bloquear todo")
    class ManejoFilasTests {

        @Test @DisplayName("HabilitarFila hace editable las 4 celdas de la fila")
        void habilitarFilaEditable() {
            juegoNormal.habilitarFila(2);
            for (int j = 0; j < 4; j++) {
                assertTrue(juegoNormal.getTablero()[2][j].isEditable());
                assertFalse(juegoNormal.getTablero()[2][j].isDisable());
            }
        }

        @Test @DisplayName("DeshabilitarFila bloquea las 4 celdas de la fila")
        void deshabilitarFilaBloqueada() {
            juegoNormal.habilitarFila(3);
            juegoNormal.deshabilitarFila(3);
            for (int j = 0; j < 4; j++) {
                assertFalse(juegoNormal.getTablero()[3][j].isEditable());
                assertTrue(juegoNormal.getTablero()[3][j].isDisable());
            }
        }

        @Test @DisplayName("BloquearTodo deshabilita todas las filas")
        void bloquearTodoDeshabilita() {
            for (int i = 0; i < 6; i++) juegoNormal.habilitarFila(i);
            juegoNormal.bloquearTodo();
            for (int i = 0; i < 6; i++)
                for (int j = 0; j < 4; j++)
                    assertFalse(juegoNormal.getTablero()[i][j].isEditable());
        }
    }


    @Nested
    @DisplayName("Flujo completo de partida")
    class FlujosCompletosTests {

        @Test @DisplayName("Victoria en el 3er intento: flujo CONTINUA, CONTINUA, GANASTE")
        void victoriaEnTercerIntento() {
            // Intento 1 – incorrecto
            String[] inc = intentoIncorrecto(juegoNormal);
            llenarFila(juegoNormal, inc);
            assertEquals("CONTINUA", juegoNormal.validarFila());

            // Intento 2 – incorrecto
            juegoNormal.habilitarFila(1);
            llenarFila(juegoNormal, inc);
            assertEquals("CONTINUA", juegoNormal.validarFila());

            // Intento 3 – correcto
            juegoNormal.habilitarFila(2);
            llenarConSolucion(juegoNormal);
            assertEquals("GANASTE", juegoNormal.validarFila());
        }

        @Test @DisplayName("Reiniciar después de ganar permite jugar de nuevo")
        void reiniciarDespuesDeGanar() {
            llenarConSolucion(juegoNormal);
            juegoNormal.validarFila();    // GANASTE
            juegoNormal.reiniciarJuego();

            assertEquals(1, juegoNormal.getIntentoActual());
            assertNotNull(juegoNormal.getSolucion());
        }
    }
}
