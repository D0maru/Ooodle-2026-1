package unitarias;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ftgw.ooodle.Modelo.RelojDiario;
import ftgw.ooodle.Modelo.ResultadoPartida;
import ftgw.ooodle.Modelo.SesionUsuario;
import ftgw.ooodle.Modelo.Usuario;

import static org.junit.jupiter.api.Assertions.*;

// ═══════════════════════════════════════════════════════════════════════════════
//  SesionUsuario
// ═══════════════════════════════════════════════════════════════════════════════
@DisplayName("Pruebas unitarias — SesionUsuario")
public class ISesionUsuario {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        SesionUsuario.resetInstancia();
        usuario = new Usuario(10, "testUser", false);
    }

    @AfterEach
    void tearDown() {
        SesionUsuario.resetInstancia();
    }

    @Test
    @DisplayName("iniciarSesion crea la instancia correctamente")
    void testIniciarSesion() {
        SesionUsuario sesion = SesionUsuario.iniciarSesion(usuario);
        assertNotNull(sesion);
        assertEquals(usuario, sesion.getUsuarioActual());
    }

    @Test
    @DisplayName("getInstancia retorna la sesión activa")
    void testGetInstancia() {
        SesionUsuario.iniciarSesion(usuario);
        SesionUsuario sesion = SesionUsuario.getInstancia();
        assertNotNull(sesion);
        assertEquals(usuario, sesion.getUsuarioActual());
    }

    @Test
    @DisplayName("getInstancia lanza excepción si no hay sesión activa")
    void testGetInstanciaSinSesion() {
        assertThrows(IllegalStateException.class, SesionUsuario::getInstancia);
    }

    @Test
    @DisplayName("iniciarSesion con null lanza IllegalArgumentException")
    void testIniciarSesionConNull() {
        assertThrows(IllegalArgumentException.class,
            () -> SesionUsuario.iniciarSesion(null));
    }

    @Test
    @DisplayName("haySesionActiva retorna false antes de iniciar sesión")
    void testHaySesionActivaFalse() {
        assertFalse(SesionUsuario.haySesionActiva());
    }

    @Test
    @DisplayName("haySesionActiva retorna true después de iniciar sesión")
    void testHaySesionActivaTrue() {
        SesionUsuario.iniciarSesion(usuario);
        assertTrue(SesionUsuario.haySesionActiva());
    }

    @Test
    @DisplayName("resetInstancia elimina la sesión activa")
    void testResetInstancia() {
        SesionUsuario.iniciarSesion(usuario);
        assertTrue(SesionUsuario.resetInstancia());
        assertFalse(SesionUsuario.haySesionActiva());
    }

    @Test
    @DisplayName("resetInstancia retorna false si no había sesión")
    void testResetSinSesion() {
        assertFalse(SesionUsuario.resetInstancia());
    }

    @Test
    @DisplayName("setUsuarioActual cambia el usuario y retorna this")
    void testSetUsuarioActual() {
        SesionUsuario sesion = SesionUsuario.iniciarSesion(usuario);
        Usuario nuevo = new Usuario(99, "nuevoUser", true);
        SesionUsuario retorno = sesion.setUsuarioActual(nuevo);
        assertSame(sesion, retorno);
        assertEquals(nuevo, sesion.getUsuarioActual());
    }

    @Test
    @DisplayName("iniciarSesion reemplaza una sesión existente")
    void testIniciarSesionReemplaza() {
        SesionUsuario.iniciarSesion(usuario);
        Usuario otro = new Usuario(2, "otroUser", true);
        SesionUsuario.iniciarSesion(otro);
        assertEquals(otro, SesionUsuario.getInstancia().getUsuarioActual());
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
//  ResultadoPartida
// ═══════════════════════════════════════════════════════════════════════════════

class ResultadoPartidaTest {

    private ResultadoPartida resultado;

    @BeforeEach
    void setUp() {
        resultado = new ResultadoPartida(1, 1, 1, 1);
    }

    @Test
    @DisplayName("Constructor asigna todos los campos correctamente")
    void testConstructor() {
        assertEquals(1, resultado.getIdUsuario());
        assertEquals(1, resultado.getCambioRacha());
        assertEquals(1, resultado.getCambioGanadas());
        assertEquals(1, resultado.getCambioJugadas());
    }

    @Test
    @DisplayName("setIdUsuario retorna true")
    void testSetIdUsuario() {
        assertTrue(resultado.setIdUsuario(5));
        assertEquals(5, resultado.getIdUsuario());
    }

    @Test
    @DisplayName("setCambioRacha retorna true")
    void testSetCambioRacha() {
        assertTrue(resultado.setCambioRacha(-1));
        assertEquals(-1, resultado.getCambioRacha());
    }

    @Test
    @DisplayName("setCambioGanadas retorna true")
    void testSetCambioGanadas() {
        assertTrue(resultado.setCambioGanadas(0));
        assertEquals(0, resultado.getCambioGanadas());
    }

    @Test
    @DisplayName("setCambioJugadas retorna true")
    void testSetCambioJugadas() {
        assertTrue(resultado.setCambioJugadas(2));
        assertEquals(2, resultado.getCambioJugadas());
    }

    @Test
    @DisplayName("Encadenamiento de setters fluentes funciona correctamente")
    void testEncadenamiento() {
        ResultadoPartida r = resultado
            .setRachaActual(5)
            .setRachaMax(10)
            .setPartidasJugadas(20)
            .setPartidasGanadas(15);

        assertSame(resultado, r);
        assertEquals(5,  resultado.rachaActual);
        assertEquals(10, resultado.rachaMax);
        assertEquals(20, resultado.partidasJugadas);
        assertEquals(15, resultado.partidasGanadas);
    }
}


// ═══════════════════════════════════════════════════════════════════════════════
//  RelojDiario
// ═══════════════════════════════════════════════════════════════════════════════

class RelojDiarioTest {

    @Test
    @DisplayName("isPuedeJugarInicial retorna el valor pasado al constructor")
    void testPuedeJugarInicial() {
        RelojDiario puedeJugar = new RelojDiario(true);
        RelojDiario noPuedeJugar = new RelojDiario(false);

        assertTrue(puedeJugar.isPuedeJugarInicial());
        assertFalse(noPuedeJugar.isPuedeJugarInicial());
    }

    @Test
    @DisplayName("getTiempoRestante retorna formato HH:MM:SS válido")
    void testFormatoTiempoRestante() {
        RelojDiario reloj = new RelojDiario(true);
        String tiempo = reloj.getTiempoRestante();
        // Debe coincidir con HH:MM:SS
        assertTrue(tiempo.matches("\\d{2}:\\d{2}:\\d{2}"),
            "Formato inesperado: " + tiempo);
    }

    @Test
    @DisplayName("getTiempoRestante tiene horas entre 0 y 23")
    void testHorasEnRango() {
        RelojDiario reloj = new RelojDiario(false);
        String tiempo = reloj.getTiempoRestante();
        int horas = Integer.parseInt(tiempo.split(":")[0]);
        assertTrue(horas >= 0 && horas <= 23);
    }

    @Test
    @DisplayName("puedeJugar retorna true si puedeJugarInicial es true")
    void testPuedeJugarConPermiso() {
        RelojDiario reloj = new RelojDiario(true);
        assertTrue(reloj.puedeJugar());
    }

    @Test
    @DisplayName("puedeJugar retorna false si no tiene permiso y no es medianoche")
    void testNoPuedeJugar() {
        // Con false, sólo puede jugar si el tiempo restante es <= 0 (medianoche exacta),
        // lo cual no ocurre en un test normal
        RelojDiario reloj = new RelojDiario(false);
        // En condiciones normales (no es medianoche) debe ser false
        // Nota: si el test se corre exactamente a medianoche puede fallar; es aceptable.
        String tiempo = reloj.getTiempoRestante();
        if (!tiempo.equals("00:00:00")) {
            assertFalse(reloj.puedeJugar());
        }
    }
}