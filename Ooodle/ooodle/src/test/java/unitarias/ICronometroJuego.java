package unitarias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ftgw.ooodle.Modelo.CronometroJuego;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias — CronometroJuego")
public class ICronometroJuego {

    private CronometroJuego cronometro;

    @BeforeEach
    void setUp() {
        cronometro = new CronometroJuego();
    }

    @Test
    @DisplayName("Estado inicial: 0 segundos transcurridos")
    void testEstadoInicial() {
        assertEquals(0, cronometro.getSegundosTranscurridos());
    }

    @Test
    @DisplayName("Estado inicial: tiempo formateado es 00:00")
    void testTiempoInicialFormateado() {
        assertEquals("00:00", cronometro.obtenerTiempoFormateado());
    }

    @Test
    @DisplayName("incrementoSegundos incrementa el contador")
    void testIncremento() {
        cronometro.incrementoSegundos();
        assertEquals(1, cronometro.getSegundosTranscurridos());
    }

    @Test
    @DisplayName("incrementoSegundos retorna tiempo formateado correcto")
    void testIncrementoRetornaFormato() {
        String tiempo = cronometro.incrementoSegundos();
        assertEquals("00:01", tiempo);
    }

    @Test
    @DisplayName("Formato correcto al pasar 1 minuto exacto")
    void testFormatoUnMinuto() {
        for (int i = 0; i < 60; i++) cronometro.incrementoSegundos();
        assertEquals("01:00", cronometro.obtenerTiempoFormateado());
    }

    @Test
    @DisplayName("Formato correcto a los 90 segundos")
    void testFormato90Segundos() {
        for (int i = 0; i < 90; i++) cronometro.incrementoSegundos();
        assertEquals("01:30", cronometro.obtenerTiempoFormateado());
    }

    @Test
    @DisplayName("esTiempoMaximo retorna false antes de 3600 segundos")
    void testNoEsTiempoMaximo() {
        for (int i = 0; i < 3599; i++) cronometro.incrementoSegundos();
        assertFalse(cronometro.esTiempoMaximo());
    }

    @Test
    @DisplayName("esTiempoMaximo retorna true al llegar a 3600 segundos")
    void testEsTiempoMaximo() {
        for (int i = 0; i < 3600; i++) cronometro.incrementoSegundos();
        assertTrue(cronometro.esTiempoMaximo());
    }

    @Test
    @DisplayName("Mensaje especial al superar el máximo de tiempo")
    void testMensajeTiempoMaximo() {
        for (int i = 0; i < 3600; i++) cronometro.incrementoSegundos();
        assertEquals("Te demoraste mucho!", cronometro.obtenerTiempoFormateado());
    }

    @Test
    @DisplayName("reiniciar vuelve a 0 segundos y retorna 00:00")
    void testReiniciar() {
        for (int i = 0; i < 50; i++) cronometro.incrementoSegundos();
        String resultado = cronometro.reiniciar();
        assertEquals(0, cronometro.getSegundosTranscurridos());
        assertEquals("00:00", resultado);
    }
}