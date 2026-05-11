package unitarias;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ftgw.ooodle.Modelo.Usuario;

@DisplayName("Usuario: gestión de perfil de jugador")
class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1, "Jugador1", false);
    }

    @Test
    @DisplayName("Constructor asigna correctamente id, nickname y juegoDiario")
    void constructorAsignaCampos() {
        assertEquals(1, usuario.getId());
        assertEquals("Jugador1", usuario.getNickname());
    }

    @Test
    @DisplayName("getPuedeJugar retorna false por defecto (no seteado)")
    void puedeJugarFalsePorDefecto() {
        assertFalse(usuario.isPuedeJugar());
    }

    @Test
    @DisplayName("setPuedeJugar(true) habilita el permiso de jugar")
    void setPuedeJugarTrue() {
        usuario.setPuedeJugar(true);
        assertTrue(usuario.isPuedeJugar());
    }

    @Test
    @DisplayName("setPuedeJugar(false) deshabilita el permiso de jugar")
    void setPuedeJugarFalse() {
        usuario.setPuedeJugar(true);
        usuario.setPuedeJugar(false);
        assertFalse(usuario.isPuedeJugar());
    }

    @Test
    @DisplayName("Dos usuarios con distinto id son independientes")
    void dosUsuariosIndependientes() {
        Usuario otro = new Usuario(2, "Jugador2", true);
        otro.setPuedeJugar(true);

        assertNotEquals(usuario.getId(), otro.getId());
        assertFalse(usuario.isPuedeJugar());  // el primero no fue afectado
        assertTrue(otro.isPuedeJugar());
    }

    @Test
    @DisplayName("Nickname no es null ni vacío al crear usuario")
    void nicknameNoNullNiVacio() {
        assertNotNull(usuario.getNickname());
        assertFalse(usuario.getNickname().isBlank());
    }
}

