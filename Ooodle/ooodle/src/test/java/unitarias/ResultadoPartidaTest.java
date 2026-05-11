package unitarias;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
import ftgw.ooodle.Modelo.ResultadoPartida;

@DisplayName("ResultadoPartida: registro de estadísticas de una partida")
class ResultadoPartidaTest {

    // =========================================================================
    // BLOQUE 1 – Constructor
    // =========================================================================

    @Nested
    @DisplayName("Constructor: asignación de campos iniciales")
    class ConstructorTests {

        @Test
        @DisplayName("Constructor asigna idUsuario correctamente")
        void constructorAsignaIdUsuario() {
            ResultadoPartida r = new ResultadoPartida(42, 1, 1, 1);
            assertEquals(42, r.idUsuario);
        }

        @ParameterizedTest(name = "cambioRacha={0}, cambioGanadas={1}, cambioJugadas={2}")
        @CsvSource({
            "1, 1, 1",    // Victoria normal
            "0, 0, 1",    // Derrota (jugó pero no ganó)
            "-1, 0, 1",   // Derrota con racha rota
            "0, 0, 0",    // Estado neutro
        })
        @DisplayName("Constructor asigna cambios de estadísticas correctamente")
        void constructorAsignaCambios(int cambioRacha, int cambioGanadas, int cambioJugadas) {
            ResultadoPartida r = new ResultadoPartida(1, cambioRacha, cambioGanadas, cambioJugadas);
            assertEquals(cambioRacha, r.cambioRacha);
            assertEquals(cambioGanadas, r.cambioGanadas);
            assertEquals(cambioJugadas, r.cambioJugadas);
        }
    }

    // =========================================================================
    // BLOQUE 2 – Setters de totales desde BD
    // =========================================================================

    @Nested
    @DisplayName("Setters: totales cargados desde la base de datos")
    class SettersTests {

        private ResultadoPartida resultado;

        @BeforeEach
        void setUp() {
            resultado = new ResultadoPartida(1, 1, 1, 1);
        }

        @Test
        @DisplayName("setRachaActual guarda el valor correctamente")
        void setRachaActual() {
            resultado.setRachaActual(5);
            assertEquals(5, resultado.rachaActual);
        }

        @Test
        @DisplayName("setRachaMax guarda el valor correctamente")
        void setRachaMax() {
            resultado.setRachaMax(10);
            assertEquals(10, resultado.rachaMax);
        }

        @Test
        @DisplayName("setPartidasJugadas guarda el valor correctamente")
        void setPartidasJugadas() {
            resultado.setPartidasJugadas(20);
            assertEquals(20, resultado.partidasJugadas);
        }

        @Test
        @DisplayName("setPartidasGanadas guarda el valor correctamente")
        void setPartidasGanadas() {
            resultado.setPartidasGanadas(15);
            assertEquals(15, resultado.partidasGanadas);
        }

        @Test
        @DisplayName("Todos los setters son independientes entre sí")
        void settersIndependientes() {
            resultado.setRachaActual(3);
            resultado.setRachaMax(7);
            resultado.setPartidasJugadas(12);
            resultado.setPartidasGanadas(8);

            assertEquals(3, resultado.rachaActual);
            assertEquals(7, resultado.rachaMax);
            assertEquals(12, resultado.partidasJugadas);
            assertEquals(8, resultado.partidasGanadas);
        }
    }

    // =========================================================================
    // BLOQUE 3 – Escenarios del juego
    // =========================================================================

    @Nested
    @DisplayName("Escenarios del juego Fill the Grid")
    class EscenariosJuegoTests {

        @Test
        @DisplayName("Escenario victoria: racha +1, ganadas +1, jugadas +1")
        void escenarioVictoria() {
            ResultadoPartida victoria = new ResultadoPartida(7, 1, 1, 1);
            assertEquals(1, victoria.cambioRacha);
            assertEquals(1, victoria.cambioGanadas);
            assertEquals(1, victoria.cambioJugadas);
        }

        @Test
        @DisplayName("Escenario derrota: racha 0 o negativo, ganadas 0, jugadas +1")
        void escenarioDerrota() {
            ResultadoPartida derrota = new ResultadoPartida(7, 0, 0, 1);
            assertEquals(0, derrota.cambioRacha);
            assertEquals(0, derrota.cambioGanadas);
            assertEquals(1, derrota.cambioJugadas);
        }

        @Test
        @DisplayName("rachaActual puede ser mayor que rachaMax (esperando actualización)")
        void rachaActualMayorQueMax() {
            ResultadoPartida r = new ResultadoPartida(1, 1, 1, 1);
            r.setRachaActual(8);
            r.setRachaMax(5); // BD aún no actualizada
            assertTrue(r.rachaActual > r.rachaMax);
        }

        @Test
        @DisplayName("partidasGanadas no supera partidasJugadas en escenario real")
        void ganadas_noSuperaJugadas() {
            ResultadoPartida r = new ResultadoPartida(1, 1, 1, 1);
            r.setPartidasJugadas(10);
            r.setPartidasGanadas(7);
            assertTrue(r.partidasGanadas <= r.partidasJugadas);
        }
    }
}

