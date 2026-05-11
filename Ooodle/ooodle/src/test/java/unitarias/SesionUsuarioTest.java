package unitarias;

import org.junit.jupiter.api.*;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;
import ftgw.ooodle.Modelo.Usuario;
import ftgw.ooodle.Modelo.SesionUsuario;

@DisplayName("SesionUsuario: Singleton de sesión activa")
class SesionUsuarioTest {

    /**
     * Resetea el Singleton antes de cada prueba para garantizar aislamiento.
     * SesionUsuario guarda estado estático, así que necesitamos limpiarlo.
     */
    @BeforeEach
    void resetSingleton() throws Exception {
        Field instanciaField = SesionUsuario.class.getDeclaredField("instancia");
        instanciaField.setAccessible(true);
        instanciaField.set(null, null);
    }

    @Test
    @DisplayName("getInstancia() nunca retorna null")
    void getInstanciaNoEsNull() {
        assertNotNull(SesionUsuario.getInstancia());
    }

    @Test
    @DisplayName("Siempre retorna la misma instancia (patrón Singleton)")
    void mismaInstanciaEnLlamadasMultiples() {
        SesionUsuario primera = SesionUsuario.getInstancia();
        SesionUsuario segunda = SesionUsuario.getInstancia();
        assertSame(primera, segunda);
    }

    @Test
    @DisplayName("getUsuarioActual() retorna null si no se ha seteado un usuario")
    void usuarioActualNullPorDefecto() {
        assertNull(SesionUsuario.getInstancia().getUsuarioActual());
    }

    @Test
    @DisplayName("setUsuarioActual guarda y getUsuarioActual recupera el mismo usuario")
    void setYGetUsuarioActual() {
        Usuario usuario = new Usuario(5, "TestPlayer", false);
        SesionUsuario sesion = SesionUsuario.getInstancia();

        sesion.setUsuarioActual(usuario);

        assertSame(usuario, sesion.getUsuarioActual());
        assertEquals("TestPlayer", sesion.getUsuarioActual().getNickname());
        assertEquals(5, sesion.getUsuarioActual().getId());
    }

    @Test
    @DisplayName("setUsuarioActual puede sobreescribir el usuario actual")
    void sobreescribirUsuarioActual() {
        Usuario jugador1 = new Usuario(1, "Uno", false);
        Usuario jugador2 = new Usuario(2, "Dos", true);
        SesionUsuario sesion = SesionUsuario.getInstancia();

        sesion.setUsuarioActual(jugador1);
        sesion.setUsuarioActual(jugador2);

        assertEquals("Dos", sesion.getUsuarioActual().getNickname());
    }

    @Test
    @DisplayName("setUsuarioActual acepta null (cerrar sesión)")
    void setUsuarioNullCierraSesion() {
        SesionUsuario sesion = SesionUsuario.getInstancia();
        sesion.setUsuarioActual(new Usuario(1, "Alguien", false));
        sesion.setUsuarioActual(null);
        assertNull(sesion.getUsuarioActual());
    }
}

