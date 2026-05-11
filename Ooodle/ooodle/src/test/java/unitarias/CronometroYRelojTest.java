package unitarias;

import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para CronometroJuego y RelojDiario.
 *
 * Ambas clases usan javafx.animation.Timeline y javafx.scene.control.Label,
 * que requieren el toolkit JavaFX inicializado. Para aislar la lógica pura:
 *
 *   - Se prueban las clases stub equivalentes que replican la lógica sin JavaFX.
 *   - Se valida el formateo de tiempo, los límites de una hora y la lógica
 *     del reloj diario (habilitar/deshabilitar botón según puedeJugarInicial).
 *
 * Las pruebas de integración con JavaFX se realizan en una suite separada
 * que inicializa el toolkit (JFXPanel o TestFX).
 */
@DisplayName("CronometroJuego y RelojDiario: lógica de tiempo y acceso diario")
class CronometroYRelojTest {

    // =========================================================================
    // Stub de CronometroJuego (lógica pura, sin JavaFX)
    // =========================================================================

    static class CronometroStub {
        private int segundosTranscurridos = 0;
        private String textoActual = "00:00";
        private boolean detenido = false;

        public void inicializar() {
            segundosTranscurridos = 0;
            textoActual = "00:00";
            detenido = false;
        }

        public void avanzarSegundo() {
            if (detenido) return;
            segundosTranscurridos++;
            actualizarTexto();
        }

        private void actualizarTexto() {
            if (segundosTranscurridos >= 3600) {
                textoActual = "¡Te demoraste mucho!";
                detenido = true;
                return;
            }
            int min = segundosTranscurridos / 60;
            int seg = segundosTranscurridos % 60;
            textoActual = String.format("%02d:%02d", min, seg);
        }

        public void detener() { detenido = true; }

        public void reiniciar() {
            segundosTranscurridos = 0;
            actualizarTexto();
            detenido = false;
        }

        public String getTexto()            { return textoActual; }
        public int getSegundos()            { return segundosTranscurridos; }
        public boolean isDetenido()         { return detenido; }
    }

    // =========================================================================
    // Stub de RelojDiario (lógica pura, sin JavaFX)
    // =========================================================================

    static class RelojDiarioStub {
        private final boolean puedeJugarInicial;
        private boolean botonHabilitado;
        private String textoReloj = "";

        RelojDiarioStub(boolean puedeJugarInicial) {
            this.puedeJugarInicial = puedeJugarInicial;
            // Al iniciar, si puede jugar, el botón se habilita inmediatamente
            this.botonHabilitado = puedeJugarInicial;
        }

        /** Simula un tick del Timeline con N segundos restantes hasta medianoche */
        public void tick(long segundosRestantes) {
            if (segundosRestantes <= 0) {
                botonHabilitado = true;
                textoReloj = "00:00:00";
            } else {
                if (!puedeJugarInicial) {
                    botonHabilitado = false;
                }
                long h = segundosRestantes / 3600;
                long m = (segundosRestantes % 3600) / 60;
                long s = segundosRestantes % 60;
                textoReloj = String.format("%02d:%02d:%02d", h, m, s);
            }
        }

        public boolean isBotonHabilitado() { return botonHabilitado; }
        public String getTextoReloj()       { return textoReloj; }
    }

    // =========================================================================
    // BLOQUE 1 – CronometroStub: formateo y comportamiento
    // =========================================================================

    @Nested
    @DisplayName("CronometroJuego: formateo de tiempo y límites")
    class CronometroTests {

        private CronometroStub crono;

        @BeforeEach
        void setUp() {
            crono = new CronometroStub();
            crono.inicializar();
        }

        @Test @DisplayName("Estado inicial muestra '00:00'")
        void estadoInicialCerosCero() {
            assertEquals("00:00", crono.getTexto());
            assertEquals(0, crono.getSegundos());
        }

        @Test @DisplayName("Avanzar 1 segundo muestra '00:01'")
        void unSegundo() {
            crono.avanzarSegundo();
            assertEquals("00:01", crono.getTexto());
        }

        @Test @DisplayName("Avanzar 59 segundos muestra '00:59'")
        void cincuentaNueveSegundos() {
            for (int i = 0; i < 59; i++) crono.avanzarSegundo();
            assertEquals("00:59", crono.getTexto());
        }

        @Test @DisplayName("Avanzar 60 segundos muestra '01:00'")
        void sesentaSegundos() {
            for (int i = 0; i < 60; i++) crono.avanzarSegundo();
            assertEquals("01:00", crono.getTexto());
        }

        @Test @DisplayName("Avanzar 3599 segundos muestra '59:59'")
        void treintaYNueve59() {
            for (int i = 0; i < 3599; i++) crono.avanzarSegundo();
            assertEquals("59:59", crono.getTexto());
        }

        @Test @DisplayName("Al llegar a 3600s muestra mensaje de tiempo excedido")
        void mensajeTiempoExcedido() {
            for (int i = 0; i < 3600; i++) crono.avanzarSegundo();
            assertEquals("¡Te demoraste mucho!", crono.getTexto());
            assertTrue(crono.isDetenido());
        }

        @Test @DisplayName("Tras el mensaje de tiempo excedido, el cronómetro se detiene")
        void cronometroDetenidoTrasLimite() {
            for (int i = 0; i < 3600; i++) crono.avanzarSegundo();
            int segundosAntes = crono.getSegundos();
            crono.avanzarSegundo(); // no debe avanzar
            assertEquals(segundosAntes, crono.getSegundos());
        }

        @Test @DisplayName("Detener congela el contador")
        void detenerCongela() {
            for (int i = 0; i < 30; i++) crono.avanzarSegundo();
            crono.detener();
            crono.avanzarSegundo();
            assertEquals(30, crono.getSegundos());
        }

        @Test @DisplayName("Reiniciar vuelve a '00:00' y permite avanzar de nuevo")
        void reiniciarVuelveCero() {
            for (int i = 0; i < 45; i++) crono.avanzarSegundo();
            crono.reiniciar();
            assertEquals("00:00", crono.getTexto());
            assertEquals(0, crono.getSegundos());
            crono.avanzarSegundo();
            assertEquals("00:01", crono.getTexto());
        }

        @Test @DisplayName("Formato siempre usa dos dígitos para minutos y segundos")
        void formatoDosDigitos() {
            crono.avanzarSegundo(); // 1 segundo
            assertTrue(crono.getTexto().matches("\\d{2}:\\d{2}"),
                    "Formato inválido: " + crono.getTexto());
        }
    }

    // =========================================================================
    // BLOQUE 2 – RelojDiario: acceso diario y habilitación del botón
    // =========================================================================

    @Nested
    @DisplayName("RelojDiario: control de acceso al modo diario")
    class RelojDiarioTests {

        @Test @DisplayName("Si puedeJugarInicial=true, botón habilitado desde el inicio")
        void puedeJugar_botonHabilitadoInmediatamente() {
            RelojDiarioStub reloj = new RelojDiarioStub(true);
            assertTrue(reloj.isBotonHabilitado());
        }

        @Test @DisplayName("Si puedeJugarInicial=false, botón deshabilitado al inicio")
        void noPuedeJugar_botonDeshabilitadoAlInicio() {
            RelojDiarioStub reloj = new RelojDiarioStub(false);
            assertFalse(reloj.isBotonHabilitado());
        }

        @Test @DisplayName("Con segundosRestantes=0, el botón se habilita independientemente de puedeJugar")
        void medianoche_habilitaBoton() {
            RelojDiarioStub reloj = new RelojDiarioStub(false);
            reloj.tick(0);
            assertTrue(reloj.isBotonHabilitado());
        }

        @Test @DisplayName("Con segundosRestantes=0, el texto muestra '00:00:00'")
        void medianoche_textoReset() {
            RelojDiarioStub reloj = new RelojDiarioStub(false);
            reloj.tick(0);
            assertEquals("00:00:00", reloj.getTextoReloj());
        }

        @Test @DisplayName("Con tiempo restante y puedeJugar=false, botón sigue deshabilitado")
        void conTiempoRestante_noPuedeJugar_botonDeshabilitado() {
            RelojDiarioStub reloj = new RelojDiarioStub(false);
            reloj.tick(3600); // 1 hora restante
            assertFalse(reloj.isBotonHabilitado());
        }

        @Test @DisplayName("Con tiempo restante y puedeJugar=true, botón permanece habilitado")
        void conTiempoRestante_puedeJugar_botonHabilitado() {
            RelojDiarioStub reloj = new RelojDiarioStub(true);
            reloj.tick(7200); // 2 horas restantes
            assertTrue(reloj.isBotonHabilitado());
        }

        @Test @DisplayName("Formato del reloj: HH:MM:SS con dos dígitos cada parte")
        void formatoReloj_tresPartes() {
            RelojDiarioStub reloj = new RelojDiarioStub(false);
            reloj.tick(3661); // 1h 1m 1s
            assertEquals("01:01:01", reloj.getTextoReloj());
        }

        @Test @DisplayName("Texto correcto para 23:59:59 restantes")
        void textoMaximoRestante() {
            RelojDiarioStub reloj = new RelojDiarioStub(false);
            reloj.tick(86399); // 23*3600 + 59*60 + 59
            assertEquals("23:59:59", reloj.getTextoReloj());
        }

        @Test @DisplayName("Texto correcto para exactamente 1 hora restante")
        void textoUnaHoraRestante() {
            RelojDiarioStub reloj = new RelojDiarioStub(false);
            reloj.tick(3600);
            assertEquals("01:00:00", reloj.getTextoReloj());
        }

        @Test @DisplayName("Varios ticks consecutivos reflejan el tiempo correcto")
        void variosTicksConsecutivos() {
            RelojDiarioStub reloj = new RelojDiarioStub(false);
            reloj.tick(7200);
            assertEquals("02:00:00", reloj.getTextoReloj());
            reloj.tick(3600);
            assertEquals("01:00:00", reloj.getTextoReloj());
            reloj.tick(0);
            assertTrue(reloj.isBotonHabilitado());
        }
    }
}

