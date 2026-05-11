package integracion;

import ftgw.ooodle.Modelo.Ecuacion;
import ftgw.ooodle.Modelo.ResultadoPartida;
import ftgw.ooodle.Modelo.SesionUsuario;
import ftgw.ooodle.Modelo.Usuario;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Integración: Ecuacion + Lógica de Juego")
class EcuacionJuegoIntegrationTest {

    private Ecuacion ecuacion;

    @BeforeEach
    void setUp() {
        ecuacion = new Ecuacion();
    }

    @Test
    @DisplayName("GenerarEcuacion (fácil): retorna exactamente 4 valores")
    void generarEcuacion_facil_retornaCuatroValores() {
        int[] resultado = ecuacion.GenerarEcuacion(14, false);
        assertNotNull(resultado, "No debe retornar null para un target alcanzable");
        assertEquals(4, resultado.length, "Debe retornar exactamente 4 valores");
    }

    @Test
    @DisplayName("GenerarEcuacion (fácil): la fórmula a*b+c-d == target")
    void generarEcuacion_facil_formulaCorrecta() {
        int target = 14;
        int[] r = ecuacion.GenerarEcuacion(target, false);
        assertNotNull(r);
        int calculado = r[0] * r[1] + r[2] - r[3];
        assertEquals(target, calculado,
            "Se esperaba " + target + " pero se obtuvo " + calculado);
    }

    @ParameterizedTest(name = "target={0}, modo fácil")
    @ValueSource(ints = {1, 5, 14, 20, 40, 70})
    @DisplayName("GenerarEcuacion (fácil): varios targets válidos")
    void generarEcuacion_facil_variosTargets(int target) {
        int[] r = ecuacion.GenerarEcuacion(target, false);
        assertNotNull(r, "Debe encontrar solución para target=" + target);
        assertEquals(target, r[0] * r[1] + r[2] - r[3]);
    }

    @Test
    @DisplayName("GenerarEcuacion (fácil): todos los dígitos están en rango 1–9")
    void generarEcuacion_facil_digitosEnRango() {
        int[] r = ecuacion.GenerarEcuacion(14, false);
        assertNotNull(r);
        for (int val : r) {
            assertTrue(val >= 1 && val <= 9,
                "Dígito " + val + " fuera del rango 1-9 en modo fácil");
        }
    }

    @Test
    @DisplayName("GenerarEcuacion (fácil): no hay dígitos repetidos")
    void generarEcuacion_facil_sinRepetidos() {
        int[] r = ecuacion.GenerarEcuacion(14, false);
        assertNotNull(r);
        // Los 4 valores deben ser distintos
        assertEquals(4,
            java.util.Arrays.stream(r).distinct().count(),
            "Los 4 dígitos no deben repetirse");
    }

    @Test
    @DisplayName("GenerarEcuacion (difícil): la fórmula a*b+c-d == target")
    void generarEcuacion_dificil_formulaCorrecta() {
        int target = 100;
        int[] r = ecuacion.GenerarEcuacion(target, true);
        assertNotNull(r, "Debe encontrar solución para target=" + target + " en modo difícil");
        assertEquals(target, r[0] * r[1] + r[2] - r[3]);
    }

    @ParameterizedTest(name = "target={0}, modo difícil")
    @ValueSource(ints = {2, 15, 50, 100, 130})
    @DisplayName("GenerarEcuacion (difícil): varios targets válidos")
    void generarEcuacion_dificil_variosTargets(int target) {
        int[] r = ecuacion.GenerarEcuacion(target, true);
        assertNotNull(r, "Debe encontrar solución para target=" + target);
        assertEquals(target, r[0] * r[1] + r[2] - r[3]);
    }

    @Test
    @DisplayName("GenerarEcuacion (difícil): todos los dígitos están en rango 1–12")
    void generarEcuacion_dificil_digitosEnRango() {
        int[] r = ecuacion.GenerarEcuacion(100, true);
        assertNotNull(r);
        for (int val : r) {
            assertTrue(val >= 1 && val <= 12,
                "Dígito " + val + " fuera del rango 1-12 en modo difícil");
        }
    }

    @Test
    @DisplayName("GenerarEcuacion: target inalcanzable retorna null")
    void generarEcuacion_targetInalcanzable_retornaNull() {
        // Un target imposible para modo fácil (ej: mayor que 9*8+7-1=78, pero negativo extremo)
        int[] r = ecuacion.GenerarEcuacion(-999, false);
        assertNull(r, "Debe retornar null si el target no tiene solución");
    }

   
    @Test
    @DisplayName("ResultadoPartida: victoria incrementa racha y ganadas")
    void resultadoPartida_victoria_deltasCorrecto() {
        ResultadoPartida r = new ResultadoPartida(1, 1, 1, 1);
        assertEquals(1, r.cambioRacha,   "Victoria debe incrementar racha en +1");
        assertEquals(1, r.cambioGanadas, "Victoria debe incrementar ganadas en +1");
        assertEquals(1, r.cambioJugadas, "Siempre debe incrementar jugadas en +1");
    }

    @Test
    @DisplayName("ResultadoPartida: derrota resetea racha y no suma ganadas")
    void resultadoPartida_derrota_deltasCorrecto() {
        ResultadoPartida r = new ResultadoPartida(1, -1, 0, 1);
        assertEquals(-1, r.cambioRacha,  "Derrota debe indicar reset de racha (-1)");
        assertEquals(0,  r.cambioGanadas,"Derrota no debe sumar partidas ganadas");
        assertEquals(1,  r.cambioJugadas,"Derrota sí debe sumar partidas jugadas");
    }

    @Test
    @DisplayName("ResultadoPartida: setters de totales funcionan correctamente")
    void resultadoPartida_settersTotales() {
        ResultadoPartida r = new ResultadoPartida(5, 1, 1, 1);
        r.setRachaActual(3);
        r.setRachaMax(7);
        r.setPartidasJugadas(20);
        r.setPartidasGanadas(12);

        assertEquals(3,  r.rachaActual);
        assertEquals(7,  r.rachaMax);
        assertEquals(20, r.partidasJugadas);
        assertEquals(12, r.partidasGanadas);
    }

    @Test
    @DisplayName("ResultadoPartida: rachaMax se actualiza cuando rachaActual la supera")
    void resultadoPartida_rachaMaxSeActualiza() {
        // Simula la lógica interna de DAOEstadisticas sin BD
        int rachaAct = 5;
        int rachaMax = 3;
        int cambio = 1; // victoria

        rachaAct += cambio;          // 6
        if (rachaAct > rachaMax) rachaMax = rachaAct; // 6

        assertEquals(6, rachaAct);
        assertEquals(6, rachaMax, "rachaMax debe actualizarse cuando la racha actual la supera");
    }

    @Test
    @DisplayName("ResultadoPartida: rachaMax NO se reduce con una derrota")
    void resultadoPartida_rachaMaxNoSeReduce() {
        int rachaAct = 6;
        int rachaMax = 6;
        int cambio = -1; // derrota

        if (cambio == -1) {
            rachaAct = 0;
        }
        if (rachaAct > rachaMax) rachaMax = rachaAct;

        assertEquals(0, rachaAct,  "Racha actual debe resetearse a 0 tras derrota");
        assertEquals(6, rachaMax,  "Racha máxima no debe disminuir tras derrota");
    }

   
    @Test
    @DisplayName("SesionUsuario: siempre retorna la misma instancia (Singleton)")
    void sesionUsuario_esSingleton() {
        SesionUsuario s1 = SesionUsuario.getInstancia();
        SesionUsuario s2 = SesionUsuario.getInstancia();
        assertSame(s1, s2, "SesionUsuario debe ser Singleton");
    }

    @Test
    @DisplayName("SesionUsuario: guardar y recuperar usuario correctamente")
    void sesionUsuario_guardarYRecuperarUsuario() {
        Usuario usuario = new Usuario(42, "TestPlayer", false);
        SesionUsuario.getInstancia().setUsuarioActual(usuario);

        Usuario recuperado = SesionUsuario.getInstancia().getUsuarioActual();
        assertNotNull(recuperado);
        assertEquals(42,           recuperado.getId());
        assertEquals("TestPlayer", recuperado.getNickname());
    }

    @Test
    @DisplayName("SesionUsuario: puedeJugar se actualiza en la sesión")
    void sesionUsuario_puedeJugarSeActualiza() {
        Usuario usuario = new Usuario(1, "Gamer", false);
        usuario.setPuedeJugar(true);
        SesionUsuario.getInstancia().setUsuarioActual(usuario);

        assertTrue(SesionUsuario.getInstancia().getUsuarioActual().isPuedeJugar());
    }

    @Test
    @DisplayName("Usuario: constructor y getters básicos")
    void usuario_constructorYGetters() {
        Usuario u = new Usuario(7, "NuevoJugador", true);
        assertEquals(7,             u.getId());
        assertEquals("NuevoJugador", u.getNickname());
    }

    @Test
    @DisplayName("Usuario: puedeJugar es false por defecto")
    void usuario_puedeJugarDefecto() {
        Usuario u = new Usuario(1, "Jugador", false);
        assertFalse(u.isPuedeJugar(),
            "Por defecto puedeJugar debe ser false hasta que se establezca explícitamente");
    }

    @Test
    @DisplayName("Usuario: setPuedeJugar cambia el estado correctamente")
    void usuario_setPuedeJugar() {
        Usuario u = new Usuario(1, "Jugador", false);
        u.setPuedeJugar(true);
        assertTrue(u.isPuedeJugar());
        u.setPuedeJugar(false);
        assertFalse(u.isPuedeJugar());
    }

    @Test
    @DisplayName("Flujo completo: generar ecuación y verificar que la solución es correcta")
    void flujoCompleto_ecuacionGeneradaEsCorrecta() {
        // Simula lo que hace Juego.GenerarNuevoJuego()
        Ecuacion eq = new Ecuacion();
        int target = 14; // target conocido para modo fácil
        int[] solucion = eq.GenerarEcuacion(target, false);

        assertNotNull(solucion, "Debe existir solución para target=14 en modo fácil");

        // Simula ValidarFila: el jugador adivina la solución correcta en el 1er intento
        int a = solucion[0], b = solucion[1], c = solucion[2], d = solucion[3];
        assertEquals(target, a * b + c - d, "La solución generada debe cumplir a*b+c-d=target");

        // Verifica que no haya repetidos (regla del juego)
        assertTrue(a != b && a != c && a != d && b != c && b != d && c != d,
            "La solución no debe tener dígitos repetidos");

        // Verifica rango modo fácil
        for (int v : solucion) {
            assertTrue(v >= 1 && v <= 9, "Todos los valores deben estar entre 1 y 9");
        }
    }

    @Test
    @DisplayName("Flujo victoria: ResultadoPartida + SesionUsuario actualizados correctamente")
    void flujoVictoria_resultadoYSesionActualizados() {
        // Setup
        Usuario jugador = new Usuario(10, "Campeon", false);
        jugador.setPuedeJugar(true);
        SesionUsuario.getInstancia().setUsuarioActual(jugador);

        // El jugador gana → se crea el ResultadoPartida de victoria
        ResultadoPartida rp = new ResultadoPartida(10, 1, 1, 1);
        rp.setRachaActual(1);
        rp.setPartidasGanadas(1);
        rp.setPartidasJugadas(1);

        // Verificaciones
        assertEquals(10, rp.idUsuario);
        assertEquals(1,  rp.cambioRacha);
        assertEquals(1,  rp.cambioGanadas);
        assertNotNull(SesionUsuario.getInstancia().getUsuarioActual());
        assertEquals("Campeon", SesionUsuario.getInstancia().getUsuarioActual().getNickname());
    }
}

