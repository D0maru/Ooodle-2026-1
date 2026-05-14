package unitarias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ftgw.ooodle.Modelo.Usuario;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias — Usuario")
public class IUsuario {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1, "jugador1", false);
    }

    // ─── Constructor ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Constructor asigna id correctamente")
    void testConstructorId() {
        assertEquals(1, usuario.getId());
    }

    @Test
    @DisplayName("Constructor asigna nickname correctamente")
    void testConstructorNickname() {
        assertEquals("jugador1", usuario.getNickname());
    }

    @Test
    @DisplayName("Constructor asigna juegoDiario correctamente")
    void testConstructorJuegoDiario() {
        assertFalse(usuario.isJuegoDiario());

        Usuario u2 = new Usuario(2, "jugador2", true);
        assertTrue(u2.isJuegoDiario());
    }

    // ─── setJuegoDiario ───────────────────────────────────────────────────────

    @Test
    @DisplayName("setJuegoDiario retorna true al asignar true")
    void testSetJuegoDiarioTrue() {
        assertTrue(usuario.setJuegoDiario(true));
        assertTrue(usuario.isJuegoDiario());
    }

    @Test
    @DisplayName("setJuegoDiario retorna true al asignar false")
    void testSetJuegoDiarioFalse() {
        usuario.setJuegoDiario(true);
        assertTrue(usuario.setJuegoDiario(false));
        assertFalse(usuario.isJuegoDiario());
    }

    // ─── setPuedeJugar ────────────────────────────────────────────────────────

    @Test
    @DisplayName("setPuedeJugar asigna true y retorna true")
    void testSetPuedeJugarTrue() {
        assertTrue(usuario.setPuedeJugar(true));
        assertTrue(usuario.isPuedeJugar());
    }

    @Test
    @DisplayName("setPuedeJugar asigna false y retorna false")
    void testSetPuedeJugarFalse() {
        usuario.setPuedeJugar(true);
        assertFalse(usuario.setPuedeJugar(false));
        assertFalse(usuario.isPuedeJugar());
    }
}