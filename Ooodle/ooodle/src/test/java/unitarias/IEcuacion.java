package unitarias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ftgw.ooodle.Modelo.Ecuacion;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias — Ecuacion")
public class IEcuacion {

    private Ecuacion ecuacion;

    @BeforeEach
    void setUp() {
        ecuacion = new Ecuacion();
    }

    // ─── GenerarEcuacion modo fácil ───────────────────────────────────────────

    @Test
    @DisplayName("GenerarEcuacion fácil devuelve array de 4 elementos")
    void testFacilDevuelveCuatroElementos() {
        int[] sol = ecuacion.GenerarEcuacion(14, false);
        assertNotNull(sol);
        assertEquals(4, sol.length);
    }

    @Test
    @DisplayName("GenerarEcuacion fácil: la ecuación (a*b)+c-d == target")
    void testFacilEcuacionCorrecta() {
        int target = 14;
        int[] sol = ecuacion.GenerarEcuacion(target, false);
        assertNotNull(sol);
        int resultado = (sol[0] * sol[1]) + sol[2] - sol[3];
        assertEquals(target, resultado);
    }

    @Test
    @DisplayName("GenerarEcuacion fácil: no hay valores repetidos")
    void testFacilSinRepetidos() {
        int[] sol = ecuacion.GenerarEcuacion(14, false);
        assertNotNull(sol);
        for (int i = 0; i < sol.length; i++) {
            for (int j = i + 1; j < sol.length; j++) {
                assertNotEquals(sol[i], sol[j],
                    "Valores repetidos en posiciones " + i + " y " + j);
            }
        }
    }

    @Test
    @DisplayName("GenerarEcuacion fácil: valores entre 1 y 9")
    void testFacilRangoValores() {
        int[] sol = ecuacion.GenerarEcuacion(14, false);
        assertNotNull(sol);
        for (int v : sol) {
            assertTrue(v >= 1 && v <= 9, "Valor fuera de rango: " + v);
        }
    }

    // ─── GenerarEcuacion modo difícil ─────────────────────────────────────────

    @Test
    @DisplayName("GenerarEcuacion difícil: la ecuación (a*b)+c-d == target")
    void testDificilEcuacionCorrecta() {
        int target = 100;
        int[] sol = ecuacion.GenerarEcuacion(target, true);
        assertNotNull(sol);
        int resultado = (sol[0] * sol[1]) + sol[2] - sol[3];
        assertEquals(target, resultado);
    }

    @Test
    @DisplayName("GenerarEcuacion difícil: valores entre 1 y 12")
    void testDificilRangoValores() {
        int[] sol = ecuacion.GenerarEcuacion(100, true);
        assertNotNull(sol);
        for (int v : sol) {
            assertTrue(v >= 1 && v <= 12, "Valor fuera de rango: " + v);
        }
    }

    @Test
    @DisplayName("GenerarEcuacion difícil: no hay valores repetidos")
    void testDificilSinRepetidos() {
        int[] sol = ecuacion.GenerarEcuacion(100, true);
        assertNotNull(sol);
        for (int i = 0; i < sol.length; i++) {
            for (int j = i + 1; j < sol.length; j++) {
                assertNotEquals(sol[i], sol[j]);
            }
        }
    }

    // ─── Target imposible ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GenerarEcuacion retorna null para target imposible")
    void testTargetImposible() {
        // Con modo fácil (max=9), el max posible es (9*8)+7-1=78 aprox.
        // Un target absurdo como 10000 no tiene solución.
        int[] sol = ecuacion.GenerarEcuacion(10000, false);
        assertNull(sol);
    }
}