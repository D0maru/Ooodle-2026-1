package unitarias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ftgw.ooodle.Modelo.Ecuacion;
import ftgw.ooodle.Modelo.Juego;
import ftgw.ooodle.Modelo.Usuario;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias — Juego")
public class UJuego {

    private Juego juego;
    private Ecuacion ecuacion;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        ecuacion = new Ecuacion();
        usuario  = new Usuario(1, "tester", false);
        juego    = new Juego(false, ecuacion, usuario);
        juego.generarNuevoJuego();
    }

    // ─── Constructor / generarNuevoJuego ──────────────────────────────────────

    @Test
    @DisplayName("generarNuevoJuego retorna un target no nulo")
    void testGenerarNuevoJuegoRetornaTarget() {
        Juego j = new Juego(false, ecuacion, usuario);
        int target = j.generarNuevoJuego();
        // El juego debe tener una solución asignada
        assertNotNull(j.getSolucion());
        assertEquals(target, j.getTarget());
    }

    @Test
    @DisplayName("El juego guarda al usuario correctamente")
    void testGetUsuario() {
        assertEquals(usuario, juego.getUsuario());
    }

    @Test
    @DisplayName("El tablero se inicializa en -1 (celdas vacías)")
    void testTableroInicializadoEnVacio() {
        Juego j = new Juego(false, ecuacion, usuario);
        j.generarNuevoJuego();
        int[][] tablero = j.getTableroDatos();
        for (int[] fila : tablero) {
            for (int celda : fila) {
                assertEquals(-1, celda);
            }
        }
    }

    @Test
    @DisplayName("intentoActual empieza en 0")
    void testIntentoActualInicial() {
        assertEquals(0, juego.getIntentoActual());
    }

    // ─── setNumeroEnCelda ─────────────────────────────────────────────────────

    @Test
    @DisplayName("setNumeroEnCelda retorna true al asignar valor válido")
    void testSetNumeroEnCeldaValido() {
        assertTrue(juego.setNumeroEnCelda(0, 5));
        assertEquals(5, juego.getTableroDatos()[0][0]);
    }

    @Test
    @DisplayName("setNumeroEnCelda retorna false con columna fuera de rango")
    void testSetNumeroEnCeldaColumnaInvalida() {
        assertFalse(juego.setNumeroEnCelda(4, 5));
        assertFalse(juego.setNumeroEnCelda(-1, 5));
    }

    // ─── borrarCelda ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("borrarCelda retorna true y deja celda en -1")
    void testBorrarCeldaValido() {
        juego.setNumeroEnCelda(0, 5);
        assertTrue(juego.borrarCelda(0));
        assertEquals(-1, juego.getTableroDatos()[0][0]);
    }

    @Test
    @DisplayName("borrarCelda retorna false con columna fuera de rango")
    void testBorrarCeldaColumnaInvalida() {
        assertFalse(juego.borrarCelda(-1));
        assertFalse(juego.borrarCelda(4));
    }

    // ─── validarIntento ───────────────────────────────────────────────────────

    @Test
    @DisplayName("validarIntento retorna null si hay celdas vacías")
    void testValidarIntentoConVacios() {
        juego.setNumeroEnCelda(0, 1);
        // columnas 1, 2, 3 siguen en -1
        assertNull(juego.validarIntento());
    }

    @Test
    @DisplayName("validarIntento retorna null si hay valores repetidos")
    void testValidarIntentoConRepetidos() {
        juego.setNumeroEnCelda(0, 3);
        juego.setNumeroEnCelda(1, 3); // repetido
        juego.setNumeroEnCelda(2, 5);
        juego.setNumeroEnCelda(3, 7);
        assertNull(juego.validarIntento());
    }

    @Test
    @DisplayName("validarIntento con solución correcta retorna todo 2 (VERDE)")
    void testValidarIntentoGanador() {
        int[] sol = juego.getSolucion();
        for (int col = 0; col < 4; col++) {
            juego.setNumeroEnCelda(col, sol[col]);
        }
        int[] resultado = juego.validarIntento();
        assertNotNull(resultado);
        for (int color : resultado) {
            assertEquals(2, color, "Se esperaba VERDE (2) para la solución exacta");
        }
    }

    @Test
    @DisplayName("validarIntento avanza intentoActual solo si la fila es válida")
    void testValidarIntentoAvanzaContador() {
        // Intento con vacíos → no avanza
        juego.validarIntento();
        assertEquals(0, juego.getIntentoActual());

        // Intento válido (con solución)
        int[] sol = juego.getSolucion();
        for (int col = 0; col < 4; col++) juego.setNumeroEnCelda(col, sol[col]);
        juego.validarIntento();
        assertEquals(1, juego.getIntentoActual());
    }

    // ─── esGanador ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("esGanador retorna false antes de cualquier intento")
    void testEsGanadorSinIntentos() {
        assertFalse(juego.esGanador());
    }

    @Test
    @DisplayName("esGanador retorna true al acertar la solución")
    void testEsGanadorConSolucionCorrecta() {
        int[] sol = juego.getSolucion();
        for (int col = 0; col < 4; col++) juego.setNumeroEnCelda(col, sol[col]);
        juego.validarIntento();
        assertTrue(juego.esGanador());
    }

    @Test
    @DisplayName("esGanador retorna false con intento incorrecto")
    void testEsGanadorConIntentoIncorrecto() {
        // Fila con valores válidos pero incorrectos
        int[] sol = juego.getSolucion();
        // Ponemos los valores rotados para que no coincida con la solución
        juego.setNumeroEnCelda(0, sol[1]);
        juego.setNumeroEnCelda(1, sol[2]);
        juego.setNumeroEnCelda(2, sol[3]);
        juego.setNumeroEnCelda(3, sol[0]);
        // Solo continuar si no hay repetidos (podría haber si la solución tiene duplicados de índice)
        int[] result = juego.validarIntento();
        if (result != null) {
            assertFalse(juego.esGanador());
        }
    }

    // ─── setters retornables ──────────────────────────────────────────────────

    @Test
    @DisplayName("setSolucion retorna true y asigna correctamente")
    void testSetSolucion() {
        int[] nuevaSol = {1, 2, 3, 4};
        assertTrue(juego.setSolucion(nuevaSol));
        assertArrayEquals(nuevaSol, juego.getSolucion());
    }

    @Test
    @DisplayName("setEcuacion retorna true y asigna correctamente")
    void testSetEcuacion() {
        Ecuacion nueva = new Ecuacion();
        assertTrue(juego.setEcuacion(nueva));
        assertEquals(nueva, juego.getEcuacion());
    }

    @Test
    @DisplayName("setTableroDatos retorna true y asigna correctamente")
    void testSetTableroDatos() {
        int[][] nuevoTablero = new int[6][4];
        assertTrue(juego.setTableroDatos(nuevoTablero));
        assertEquals(nuevoTablero, juego.getTableroDatos());
    }

    // ─── modo difícil ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("En modo difícil se genera solución con valores hasta 12")
    void testModoDificilRango() {
        Juego jDificil = new Juego(true, ecuacion, usuario);
        jDificil.generarNuevoJuego();
        int[] sol = jDificil.getSolucion();
        assertNotNull(sol);
        for (int v : sol) {
            assertTrue(v >= 1 && v <= 12, "Valor fuera del rango difícil: " + v);
        }
    }
}