package integracion;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ftgw.ooodle.Modelo.Ecuacion;
import ftgw.ooodle.Modelo.Juego;
import ftgw.ooodle.Modelo.ResultadoPartida;
import ftgw.ooodle.Modelo.SesionUsuario;
import ftgw.ooodle.Modelo.Usuario;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración que verifican la interacción entre
 * Juego, Ecuacion, Usuario, SesionUsuario y ResultadoPartida.
 */
@DisplayName("Pruebas de integración — Modelo")
public class IJuego {

    private Usuario usuario;
    private Ecuacion ecuacion;
    private Juego juego;

    @BeforeEach
    void setUp() {
        SesionUsuario.resetInstancia();
        usuario  = new Usuario(1, "integrador", false);
        SesionUsuario.iniciarSesion(usuario);
        ecuacion = new Ecuacion();
        juego    = new Juego(false, ecuacion, usuario);
        juego.generarNuevoJuego();
    }

    @AfterEach
    void tearDown() {
        SesionUsuario.resetInstancia();
    }

    // ─── Juego + Ecuacion ─────────────────────────────────────────────────────

    @Test
    @DisplayName("La solución generada por Ecuacion satisface la fórmula (a*b)+c-d == target")
    void testSolucionConsistenteConTarget() {
        int target = juego.getTarget();
        int[] sol  = juego.getSolucion();
        int calculado = (sol[0] * sol[1]) + sol[2] - sol[3];
        assertEquals(target, calculado);
    }

    @Test
    @DisplayName("Juego reutiliza Ecuacion correctamente tras setEcuacion")
    void testSetEcuacionYRegenerarJuego() {
        Ecuacion nuevaEcuacion = new Ecuacion();
        assertTrue(juego.setEcuacion(nuevaEcuacion));
        int nuevoTarget = juego.generarNuevoJuego();
        assertNotNull(juego.getSolucion());
        int[] sol = juego.getSolucion();
        assertEquals(nuevoTarget, (sol[0] * sol[1]) + sol[2] - sol[3]);
    }

    // ─── Juego + SesionUsuario ────────────────────────────────────────────────

    @Test
    @DisplayName("El usuario del juego coincide con el de la sesión activa")
    void testUsuarioJuegoIgualSesion() {
        Usuario sesionUser = SesionUsuario.getInstancia().getUsuarioActual();
        assertEquals(sesionUser, juego.getUsuario());
    }

    @Test
    @DisplayName("Cambiar usuario en sesión no afecta al Juego ya creado")
    void testCambioSesionNoAfectaJuego() {
        Usuario original = juego.getUsuario();
        Usuario nuevo = new Usuario(99, "otro", true);
        SesionUsuario.getInstancia().setUsuarioActual(nuevo);
        // El juego mantiene su referencia original
        assertEquals(original, juego.getUsuario());
    }

    // ─── Flujo completo de partida ganada ─────────────────────────────────────

    @Test
    @DisplayName("Flujo completo: iniciar sesión → jugar → ganar → construir ResultadoPartida")
    void testFlujoCCompletoGanador() {
        // 1. Verificar sesión activa
        assertTrue(SesionUsuario.haySesionActiva());

        // 2. Jugar con la solución correcta
        int[] sol = juego.getSolucion();
        for (int col = 0; col < 4; col++) juego.setNumeroEnCelda(col, sol[col]);
        int[] colores = juego.validarIntento();

        assertNotNull(colores);
        for (int c : colores) assertEquals(2, c); // Todo VERDE
        assertTrue(juego.esGanador());

        // 3. Construir resultado de partida
        ResultadoPartida resultado = new ResultadoPartida(usuario.getId(), 1, 1, 1)
            .setRachaActual(1)
            .setRachaMax(1)
            .setPartidasJugadas(1)
            .setPartidasGanadas(1);

        assertEquals(usuario.getId(), resultado.getIdUsuario());
        assertEquals(1, resultado.rachaActual);
        assertEquals(1, resultado.partidasGanadas);
    }

    // ─── Flujo completo de partida perdida ────────────────────────────────────

    @Test
    @DisplayName("Flujo completo: 6 intentos fallidos → no es ganador")
    void testFlujoCompletoPerdedor() {
        int[] sol = juego.getSolucion();

        // Generamos 6 intentos con los valores rotados (incorrectos pero sin repetidos)
        for (int intento = 0; intento < 6; intento++) {
            // Rotamos la solución para garantizar valores distintos entre sí
            juego.setNumeroEnCelda(0, sol[1]);
            juego.setNumeroEnCelda(1, sol[2]);
            juego.setNumeroEnCelda(2, sol[3]);
            juego.setNumeroEnCelda(3, sol[0]);

            int[] resultado = juego.validarIntento();
            // Si hay repetidos por rotación el juego rechaza; paramos
            if (resultado == null) break;
            assertFalse(juego.esGanador());
        }

        // Construir resultado de derrota
        ResultadoPartida resultado = new ResultadoPartida(usuario.getId(), -1, 0, 1);
        assertEquals(-1, resultado.getCambioRacha());
        assertEquals(0,  resultado.getCambioGanadas());
        assertEquals(1,  resultado.getCambioJugadas());
    }

    // ─── Juego modo difícil + integración ────────────────────────────────────

    @Test
    @DisplayName("Modo difícil genera solución consistente con el target")
    void testModoDificilIntegracion() {
        Juego jDificil = new Juego(true, ecuacion, usuario);
        int target = jDificil.generarNuevoJuego();
        int[] sol  = jDificil.getSolucion();
        assertNotNull(sol);
        assertEquals(target, (sol[0] * sol[1]) + sol[2] - sol[3]);
    }

    // ─── SesionUsuario + ResultadoPartida ────────────────────────────────────

    @Test
    @DisplayName("ResultadoPartida usa el id del usuario de sesión correctamente")
    void testResultadoUsaIdDeSesion() {
        int idSesion = SesionUsuario.getInstancia().getUsuarioActual().getId();
        ResultadoPartida r = new ResultadoPartida(idSesion, 1, 1, 1);
        assertEquals(idSesion, r.getIdUsuario());
    }

    @Test
    @DisplayName("Actualizar usuario en sesión tras partida refleja el cambio")
    void testActualizarUsuarioEnSesionTrasPartida() {
        // Simula lo que hace el controlador tras recibir el usuario actualizado de BD
        Usuario actualizado = new Usuario(usuario.getId(), usuario.getNickname(), false);
        actualizado.setPuedeJugar(false);
        SesionUsuario.getInstancia().setUsuarioActual(actualizado);

        Usuario enSesion = SesionUsuario.getInstancia().getUsuarioActual();
        assertEquals(usuario.getId(), enSesion.getId());
        assertFalse(enSesion.isPuedeJugar());
    }
}
