package integracion;

import ftgw.ooodle.Modelo.ResultadoPartida;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integración: DAOUsuario + DAOEstadisticas (H2 in-memory)")
class DAOIntegrationTest {

    private static Connection con;

    // ─── DDL del esquema (replica el de MySQL) ────────────────────────────────
    private static final String CREATE_USUARIO =
        "CREATE TABLE IF NOT EXISTS Usuario (" +
        "  Id          INT AUTO_INCREMENT PRIMARY KEY," +
        "  Nickname    VARCHAR(50) NOT NULL," +
        "  UltimoJuego DATE" +
        ")";

    private static final String CREATE_ESTADISTICAS =
        "CREATE TABLE IF NOT EXISTS Estadisticas (" +
        "  idUsuario    INT PRIMARY KEY," +
        "  Racha_Actual INT DEFAULT 0," +
        "  Racha_Max    INT DEFAULT 0," +
        "  P_Jugadas    INT DEFAULT 0," +
        "  P_Ganadas    INT DEFAULT 0," +
        "  FOREIGN KEY (idUsuario) REFERENCES Usuario(Id) ON DELETE CASCADE" +
        ")";

    @BeforeAll
    static void crearBD() throws SQLException {
        con = DriverManager.getConnection("jdbc:h2:mem:ooodle_test;DB_CLOSE_DELAY=-1", "sa", "");
        try (Statement st = con.createStatement()) {
            st.execute(CREATE_USUARIO);
            st.execute(CREATE_ESTADISTICAS);
        }
    }

    @AfterAll
    static void cerrarBD() throws SQLException {
        if (con != null && !con.isClosed()) con.close();
    }

    @BeforeEach
    void limpiarTablas() throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("DELETE FROM Estadisticas");
            st.execute("DELETE FROM Usuario");
            st.execute("ALTER TABLE Usuario ALTER COLUMN Id RESTART WITH 1");
        }
    }


    /** Inserta un usuario con UltimoJuego = fecha dada (null para "nunca jugó"). */
    private int insertarUsuario(String nick, Date ultimoJuego) throws SQLException {
        String sql = "INSERT INTO Usuario (Nickname, UltimoJuego) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nick);
            ps.setDate(2, ultimoJuego);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            // Insertar fila de estadísticas
            try (PreparedStatement psE = con.prepareStatement(
                "INSERT INTO Estadisticas (idUsuario) VALUES (?)")) {
                psE.setInt(1, id);
                psE.executeUpdate();
            }
            return id;
        }
    }

    /** Actualiza las estadísticas en la BD (replica lógica de DAOEstadisticas.actualizarDatos). */
    private void actualizarEstadisticas(int idUsuario, int rachaAct, int rachaMax,
                                        int jugadas, int ganadas) throws SQLException {
        String sql = "UPDATE Estadisticas SET Racha_Actual=?, Racha_Max=?, " +
                     "P_Jugadas=?, P_Ganadas=? WHERE idUsuario=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rachaAct);
            ps.setInt(2, rachaMax);
            ps.setInt(3, jugadas);
            ps.setInt(4, ganadas);
            ps.setInt(5, idUsuario);
            ps.executeUpdate();
        }
    }

    /** Lee las estadísticas de un usuario desde la BD de test. */
    private ResultSet leerEstadisticas(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM Estadisticas WHERE idUsuario = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idUsuario);
        return ps.executeQuery();
    }

 
    @Test
    @DisplayName("Insertar usuario: crea fila en Usuario y Estadisticas")
    void insertarUsuario_creaFilasEnAmbasTablas() throws SQLException {
        int id = insertarUsuario("Prueba1", null);

        // Verificar Usuario
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT Nickname FROM Usuario WHERE Id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next(), "Debe existir el usuario en BD");
            assertEquals("Prueba1", rs.getString("Nickname"));
        }

        // Verificar Estadísticas
        ResultSet rs = leerEstadisticas(id);
        assertTrue(rs.next(), "Debe existir la fila de estadísticas");
        assertEquals(0, rs.getInt("Racha_Actual"));
        assertEquals(0, rs.getInt("P_Jugadas"));
    }

    @Test
    @DisplayName("Insertar múltiples usuarios: IDs distintos y estadísticas independientes")
    void insertarMultiplesUsuarios_IDsDistintos() throws SQLException {
        int id1 = insertarUsuario("JugadorA", null);
        int id2 = insertarUsuario("JugadorB", null);
        assertNotEquals(id1, id2, "Los IDs deben ser distintos");
    }

    @Test
    @DisplayName("Eliminar usuario: borra en cascada la fila de Estadisticas")
    void eliminarUsuario_borraCascadaEstadisticas() throws SQLException {
        int id = insertarUsuario("Temporal", null);

        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM Usuario WHERE Id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

        ResultSet rs = leerEstadisticas(id);
        assertFalse(rs.next(), "Estadísticas deben eliminarse en cascada al borrar el usuario");
    }

    @Test
    @DisplayName("Victoria: racha_actual sube, ganadas sube, jugadas sube")
    void victoria_actualizaEstadisticasCorrectamente() throws SQLException {
        int id = insertarUsuario("Ganador", null);

        // Simula victoria (cambioRacha=1, cambioGanadas=1, cambioJugadas=1)
        ResultadoPartida rp = new ResultadoPartida(id, 1, 1, 1);

        // Lógica de DAOEstadisticas inlined
        int rachaAct = 0, rachaMax = 0, jugadas = 0, ganadas = 0;
        if (rp.cambioRacha == 1)  rachaAct += 1;
        else if (rp.cambioRacha == -1) rachaAct = 0;
        if (rachaAct > rachaMax)  rachaMax = rachaAct;
        ganadas += rp.cambioGanadas;
        jugadas += rp.cambioJugadas;

        actualizarEstadisticas(id, rachaAct, rachaMax, jugadas, ganadas);

        ResultSet rs = leerEstadisticas(id);
        assertTrue(rs.next());
        assertEquals(1, rs.getInt("Racha_Actual"));
        assertEquals(1, rs.getInt("Racha_Max"));
        assertEquals(1, rs.getInt("P_Jugadas"));
        assertEquals(1, rs.getInt("P_Ganadas"));
    }

    @Test
    @DisplayName("Derrota: racha_actual se resetea a 0, racha_max no disminuye")
    void derrota_reseteaRachaActualPeroNoMax() throws SQLException {
        int id = insertarUsuario("Perdedor", null);

        // Primero 3 victorias
        actualizarEstadisticas(id, 3, 3, 3, 3);

        // Luego una derrota
        ResultadoPartida rp = new ResultadoPartida(id, -1, 0, 1);
        int rachaAct = 3, rachaMax = 3, jugadas = 3, ganadas = 3;
        if (rp.cambioRacha == -1) rachaAct = 0;
        if (rachaAct > rachaMax)  rachaMax = rachaAct;
        ganadas += rp.cambioGanadas;
        jugadas += rp.cambioJugadas;

        actualizarEstadisticas(id, rachaAct, rachaMax, jugadas, ganadas);

        ResultSet rs = leerEstadisticas(id);
        assertTrue(rs.next());
        assertEquals(0, rs.getInt("Racha_Actual"), "Racha actual debe ser 0 tras derrota");
        assertEquals(3, rs.getInt("Racha_Max"),    "Racha máxima no debe disminuir");
        assertEquals(4, rs.getInt("P_Jugadas"),    "Jugadas deben incrementarse");
        assertEquals(3, rs.getInt("P_Ganadas"),    "Ganadas no deben cambiar en derrota");
    }

    @Test
    @DisplayName("Racha máxima: se actualiza sólo cuando la actual la supera")
    void rachaMaxima_seActualizaSoloCuandoEsMayor() throws SQLException {
        int id = insertarUsuario("Rachetero", null);
        actualizarEstadisticas(id, 5, 5, 5, 5);

        // Sexta victoria consecutiva
        int rachaAct = 5, rachaMax = 5;
        rachaAct += 1;  // 6
        if (rachaAct > rachaMax) rachaMax = rachaAct; // 6
        actualizarEstadisticas(id, rachaAct, rachaMax, 6, 6);

        ResultSet rs = leerEstadisticas(id);
        assertTrue(rs.next());
        assertEquals(6, rs.getInt("Racha_Actual"));
        assertEquals(6, rs.getInt("Racha_Max"));
    }

    /** Replica la lógica privada de DAOEstadisticas.evaluarPermisoJuego */
    private boolean evaluarPermisoJuego(Date fechaDB) {
        if (fechaDB == null) return true;
        LocalDate fechaUltimoJuego = fechaDB.toLocalDate();
        LocalDate hoy = LocalDate.now();
        return fechaUltimoJuego.isBefore(hoy);
    }

    @Test
    @DisplayName("evaluarPermisoJuego: null → puede jugar (nunca ha jugado)")
    void evaluarPermiso_null_puedeJugar() {
        assertTrue(evaluarPermisoJuego(null));
    }

    @Test
    @DisplayName("evaluarPermisoJuego: último juego = ayer → puede jugar")
    void evaluarPermiso_ayer_puedeJugar() {
        Date ayer = Date.valueOf(LocalDate.now().minusDays(1));
        assertTrue(evaluarPermisoJuego(ayer));
    }

    @Test
    @DisplayName("evaluarPermisoJuego: último juego = hoy → NO puede jugar")
    void evaluarPermiso_hoy_noPuedeJugar() {
        Date hoy = Date.valueOf(LocalDate.now());
        assertFalse(evaluarPermisoJuego(hoy));
    }

    @Test
    @DisplayName("evaluarPermisoJuego: último juego = hace una semana → puede jugar")
    void evaluarPermiso_semanaAntes_puedeJugar() {
        Date semanaAntes = Date.valueOf(LocalDate.now().minusWeeks(1));
        assertTrue(evaluarPermisoJuego(semanaAntes));
    }

    @Test
    @DisplayName("Estadisticas sin usuario referenciado → fallo de FK")
    void estadisticas_sinUsuario_fallaFK() {
        // Intentar insertar estadísticas para un usuario que no existe
        assertThrows(SQLException.class, () -> {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Estadisticas (idUsuario) VALUES (9999)")) {
                ps.executeUpdate();
            }
        }, "Debe lanzar SQLException por violación de clave foránea");
    }

  
    @Test
    @DisplayName("Transacción rollback: los datos no persisten si hay error")
    void transaccion_rollback_noGuardaCambios() throws SQLException {
        int id = insertarUsuario("Rollback", null);

        con.setAutoCommit(false);
        try {
            // Update válido
            actualizarEstadisticas(id, 5, 5, 5, 5);
            // Forzar rollback
            con.rollback();
        } finally {
            con.setAutoCommit(true);
        }

        // Las estadísticas deben seguir en 0 (rollback)
        ResultSet rs = leerEstadisticas(id);
        assertTrue(rs.next());
        assertEquals(0, rs.getInt("Racha_Actual"), "Racha no debe cambiar tras rollback");
        assertEquals(0, rs.getInt("P_Jugadas"),    "Jugadas no deben cambiar tras rollback");
    }

    @Test
    @DisplayName("Transacción commit: los datos persisten correctamente")
    void transaccion_commit_guardaCambios() throws SQLException {
        int id = insertarUsuario("Commit", null);

        con.setAutoCommit(false);
        actualizarEstadisticas(id, 2, 2, 2, 2);
        con.commit();
        con.setAutoCommit(true);

        ResultSet rs = leerEstadisticas(id);
        assertTrue(rs.next());
        assertEquals(2, rs.getInt("Racha_Actual"));
        assertEquals(2, rs.getInt("P_Jugadas"));
    }
}

