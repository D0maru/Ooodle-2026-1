package unitarias;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import ftgw.ooodle.Modelo.Ecuacion;

@DisplayName("Ecuacion: generación de puzzles a×b+c-d = target")
class EcuacionTest {

    /** Acceso a Evaluar (privado) vía reflexión */
    private int evaluar(int a, int b, int c, int d) throws Exception {
        var m = Ecuacion.class.getDeclaredMethod("Evaluar", int.class, int.class, int.class, int.class);
        m.setAccessible(true);
        return (int) m.invoke(new Ecuacion(), a, b, c, d);
    }

    // =========================================================================
    // BLOQUE 1 – Evaluar: fórmula a×b+c-d
    // =========================================================================

    @Nested
    @DisplayName("Evaluar: correctitud de la fórmula")
    class EvaluarTests {

        @Test @DisplayName("2×3+4-1 = 9")
        void casoBasico() throws Exception { assertEquals(9, evaluar(2, 3, 4, 1)); }

        @Test @DisplayName("Resultado cero: 1×2+3-5 = 0")
        void resultadoCero() throws Exception { assertEquals(0, evaluar(1, 2, 3, 5)); }

        @Test @DisplayName("Resultado negativo: 1×2+3-9 = -4")
        void resultadoNegativo() throws Exception { assertEquals(-4, evaluar(1, 2, 3, 9)); }

        @Test @DisplayName("Máximo modo normal: 9×8+7-1 = 78")
        void maximoNormal() throws Exception { assertEquals(78, evaluar(9, 8, 7, 1)); }

        @Test @DisplayName("Máximo modo difícil: 12×11+10-1 = 141")
        void maximoDificil() throws Exception { assertEquals(141, evaluar(12, 11, 10, 1)); }

        @Test @DisplayName("Multiplicación tiene precedencia sobre suma y resta")
        void precedenciaOperadores() throws Exception {
            // 3×4+2-1 = 13, NO (3+2-1)×4 = 16
            assertEquals(13, evaluar(3, 4, 2, 1));
        }
    }

    // =========================================================================
    // BLOQUE 2 – Estructura del resultado
    // =========================================================================

    @Nested
    @DisplayName("GenerarEcuacion: estructura y unicidad")
    class EstructuraTests {

        @Test @DisplayName("Devuelve exactamente 4 elementos")
        void retornaCuatroElementos() {
            int[] eq =  new Ecuacion().GenerarEcuacion(10, false);
            assertNotNull(eq);
            assertEquals(4, eq.length);
        }

        @Test @DisplayName("Los cuatro valores son distintos (regla del juego)")
        void valoresSinRepeticion() {
            int[] eq = new Ecuacion().GenerarEcuacion(10, false);
            assertNotNull(eq);
            Set<Integer> vistos = new HashSet<>();
            for (int v : eq)
                assertTrue(vistos.add(v), "Valor repetido: " + v);
        }

        @Test @DisplayName("La ecuación cumple a×b+c-d = target (modo normal)")
        void ecuacionCorrecta_normal() {
            int target = 15;
            int[] eq = new Ecuacion().GenerarEcuacion(target, false);
            assertNotNull(eq);
            assertEquals(target, (eq[0] * eq[1]) + eq[2] - eq[3]);
        }

        @Test @DisplayName("La ecuación cumple a×b+c-d = target (modo difícil)")
        void ecuacionCorrecta_dificil() {
            int target = 50;
            int[] eq = new Ecuacion().GenerarEcuacion(target, true);
            assertNotNull(eq);
            assertEquals(target, (eq[0] * eq[1]) + eq[2] - eq[3]);
        }
    }

    // =========================================================================
    // BLOQUE 3 – Rangos
    // =========================================================================

    @Nested
    @DisplayName("GenerarEcuacion: rangos de valores por modo")
    class RangosTests {

        @Test @DisplayName("Modo normal: valores en [1, 9]")
        void modoNormal_rango1a9() {
            int[] eq = new Ecuacion().GenerarEcuacion(10, false);
            assertNotNull(eq);
            for (int v : eq) assertTrue(v >= 1 && v <= 9, "Fuera de [1,9]: " + v);
        }

        @Test @DisplayName("Modo difícil: valores en [1, 12]")
        void modoDificil_rango1a12() {
            int[] eq = new Ecuacion().GenerarEcuacion(50, true);
            assertNotNull(eq);
            for (int v : eq) assertTrue(v >= 1 && v <= 12, "Fuera de [1,12]: " + v);
        }

        @Test @DisplayName("Modo difícil puede usar valores 10, 11 o 12 (fuera del rango normal)")
        void modoDificil_usaValoresMayoresA9() {
            // Buscar un target que solo se resuelva con números > 9
            // 12×11+10-9 = 133. Verificar que al menos uno > 9.
            int[] eq = new Ecuacion().GenerarEcuacion(113, true);;
            assertNotNull(eq);
            boolean hayMayorA9 = false;
            for (int v : eq) if (v > 9) { hayMayorA9 = true; break; }
            assertTrue(hayMayorA9, "Se esperaba al menos un valor > 9 para target=133");
        }
    }

    // =========================================================================
    // BLOQUE 4 – Targets alcanzables modo normal
    // =========================================================================

    @Nested
    @DisplayName("Targets alcanzables en modo normal [1-9], rango [-4, 78]")
    class TargetsNormalTests {

        @ParameterizedTest(name = "target={0}")
        @ValueSource(ints = {-4, -2, 0, 1, 5, 10, 15, 20, 30, 40, 50, 60, 70, 78})
        void targetAlcanzable(int target) {
            int[] eq = new Ecuacion().GenerarEcuacion(target, false);
            assertNotNull(eq, "Sin solución para target=" + target);
            assertEquals(target, (eq[0] * eq[1]) + eq[2] - eq[3]);
        }
        @Test @DisplayName("Retorna null para target=79 (por encima del máximo)")
        void demasiadoAlto() { assertNull(new Ecuacion().GenerarEcuacion(79, false)); }

        @Test @DisplayName("Retorna null para target=-5 (por debajo del mínimo)")
        void demasiadoBajo() { assertNull(new Ecuacion().GenerarEcuacion(-5, false)); }
    }

    // =========================================================================
    // BLOQUE 5 – Targets alcanzables modo difícil
    // =========================================================================

    @Nested
    @DisplayName("Targets alcanzables en modo difícil [1-12], rango [-7, 141]")
    class TargetsDificilTests {

        @ParameterizedTest(name = "target={0}")
        @ValueSource(ints = {-7, -3, 0, 10, 25, 50, 80, 100, 130, 141})
        void targetAlcanzable(int target) {
            int[] eq = new Ecuacion().GenerarEcuacion(target,true);
            assertNotNull(eq, "Sin solución para target=" + target + " (difícil)");
            assertEquals(target, (eq[0] * eq[1]) + eq[2] - eq[3]);
        }

        @Test @DisplayName("Retorna null para target=142 (por encima del máximo)")
        void demasiadoAlto() { assertNull(new Ecuacion().GenerarEcuacion(142, true)); }

        @Test @DisplayName("Retorna null para target=-8 (por debajo del mínimo)")
        void demasiadoBajo() { assertNull(new Ecuacion().GenerarEcuacion(-8, true)); }
    }

    // =========================================================================
    // BLOQUE 6 – Fallback del Juego (targets garantizados)
    // =========================================================================

    @Nested
    @DisplayName("Fallback de Juego: targets hardcodeados con solución garantizada")
    class FallbackTests {

        @Test @DisplayName("Target fallback modo normal (14) tiene solución válida")
        void fallbackNormal() {
            int[] eq = new Ecuacion().GenerarEcuacion(14,false);
            assertNotNull(eq);
            assertEquals(14, (eq[0] * eq[1]) + eq[2] - eq[3]);
        }

        @Test @DisplayName("Target fallback modo difícil (100) tiene solución válida")
       void fallbackDificil() {
            int[] eq = new Ecuacion().GenerarEcuacion(100, true); // ← correcto
            assertNotNull(eq);
            assertEquals(100, (eq[0] * eq[1]) + eq[2] - eq[3]);
        }
    }

    // =========================================================================
    // BLOQUE 7 – Consistencia
    // =========================================================================

    @Nested
    @DisplayName("Consistencia en llamadas múltiples")
    class ConsistenciaTests {

        @Test @DisplayName("10 llamadas al mismo target siempre retornan resultado válido")
        void llamadasRepetidas() {
            for (int i = 0; i < 10; i++) {
                int[] eq = new Ecuacion().GenerarEcuacion(20,false);
                assertNotNull(eq);
                assertEquals(20, (eq[0] * eq[1]) + eq[2] - eq[3], "Falla en iteración " + i);
            }
        }

        @Test @DisplayName("Modo normal y difícil con mismo target producen resultados válidos")
        void ambosModos() {
            int target = 30;
            int[] n = new Ecuacion().GenerarEcuacion(target,false);
            int[] d = new Ecuacion().GenerarEcuacion(target,true);
            assertNotNull(n);
            assertNotNull(d);
            assertEquals(target, (n[0] * n[1]) + n[2] - n[3]);
            assertEquals(target, (d[0] * d[1]) + d[2] - d[3]);
        }
    }
}

