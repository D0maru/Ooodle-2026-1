package Servicios;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDate;

public class EstadisticasService {

    private static final String RUTA = obtenerRuta();

    private static String obtenerRuta() {
        try {
            String claseUrl = EstadisticasService.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();

            File targetClasses = new File(claseUrl);
            File raiz = targetClasses.getParentFile().getParentFile();

            File json = new File(raiz,
                "src" + File.separator +
                "main" + File.separator +
                "java" + File.separator +
                "Servicios" + File.separator +
                "Estadisticas.json");

            System.out.println("[EstadisticasService] Ruta JSON: " + json.getAbsolutePath());
            return json.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return "Estadisticas.json";
        }
    }

    // =========================
    // 📥 CARGAR
    // =========================
    public static Estadisticas cargar() {
        Estadisticas stats = new Estadisticas();
        try {
            File archivo = new File(RUTA);
            System.out.println("[EstadisticasService] Cargando | existe: " + archivo.exists() + " | " + archivo.getAbsolutePath());
            if (!archivo.exists()) return stats;
            String contenido = new String(Files.readAllBytes(archivo.toPath()));
            stats.partidasJugadas   = extraerInt(contenido, "partidasJugadas");
            stats.partidasGanadas   = extraerInt(contenido, "partidasGanadas");
            stats.rachaActual       = extraerInt(contenido, "rachaActual");
            stats.rachaMaxima       = extraerInt(contenido, "rachaMaxima");
            stats.porcentajeGanadas = extraerInt(contenido, "porcentajeGanadas");
            stats.indiceAdivinanza  = extraerArray(contenido, "indiceAdivinanza");
            stats.diarioJugadoHoy   = extraerBoolean(contenido, "diarioJugadoHoy");
            stats.ultimoDiaJugado   = extraerString(contenido, "ultimoDiaJugado");

            // Logica de cambio de dia
            String hoy = LocalDate.now().toString();
            boolean esNuevoDia = !hoy.equals(stats.ultimoDiaJugado);
            if (esNuevoDia) {
                // Si existia un dia anterior y el jugador NO jugo el diario, la racha se rompe
                if (!stats.ultimoDiaJugado.isEmpty() && !stats.diarioJugadoHoy) {
                    System.out.println("[EstadisticasService] No jugo ayer -> racha a 0");
                    stats.rachaActual = 0;
                }
                // Nuevo dia: habilitar el modo diario
                stats.diarioJugadoHoy = false;
                stats.ultimoDiaJugado = hoy;
                guardar(stats);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    // =========================
    // 📤 GUARDAR
    // =========================
    public static void guardar(Estadisticas stats) {
        int n = stats.partidasJugadas;
        int w = stats.partidasGanadas;
        int derrotas = n - w;
        stats.porcentajeGanadas = (n == 0) ? 0 : (w * 100) / (w + derrotas);

        try {
            File archivo = new File(RUTA);
            archivo.getParentFile().mkdirs();
            System.out.println("[EstadisticasService] Guardando en: " + archivo.getAbsolutePath());
            FileWriter writer = new FileWriter(archivo);
            writer.write("{\n");
            writer.write("\"partidasJugadas\": "   + stats.partidasJugadas   + ",\n");
            writer.write("\"partidasGanadas\": "   + stats.partidasGanadas   + ",\n");
            writer.write("\"rachaActual\": "        + stats.rachaActual        + ",\n");
            writer.write("\"rachaMaxima\": "        + stats.rachaMaxima        + ",\n");
            writer.write("\"porcentajeGanadas\": "  + stats.porcentajeGanadas  + ",\n");
            writer.write("\"indiceAdivinanza\": [");
            for (int i = 0; i < stats.indiceAdivinanza.length; i++) {
                writer.write(String.valueOf(stats.indiceAdivinanza[i]));
                if (i < stats.indiceAdivinanza.length - 1) writer.write(", ");
            }
            writer.write("],\n");
            writer.write("\"diarioJugadoHoy\": " + stats.diarioJugadoHoy + ",\n");
            String diaActual = (stats.ultimoDiaJugado == null || stats.ultimoDiaJugado.isEmpty())
                ? LocalDate.now().toString() : stats.ultimoDiaJugado;
            writer.write("\"ultimoDiaJugado\": \"" + diaActual + "\"\n}");
            writer.close();
            System.out.println("[EstadisticasService] Guardado OK");
        } catch (Exception e) {
            System.out.println("[EstadisticasService] ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================
    // ✅ REGISTRAR VICTORIA
    // =========================
    public static void registrarVictoria(int intentoEnQueGano) {
        System.out.println("[EstadisticasService] registrarVictoria intento=" + intentoEnQueGano);
        Estadisticas stats = cargar();
        stats.partidasJugadas++;
        stats.partidasGanadas++;
        stats.rachaActual++;
        if (stats.rachaActual > stats.rachaMaxima) stats.rachaMaxima = stats.rachaActual;
        int idx = Math.max(0, Math.min(5, intentoEnQueGano - 1));
        stats.indiceAdivinanza[idx]++;
        stats.diarioJugadoHoy = true;
        stats.ultimoDiaJugado = LocalDate.now().toString();
        guardar(stats);
    }

    // =========================
    // ❌ REGISTRAR DERROTA
    // =========================
    public static void registrarDerrota() {
        System.out.println("[EstadisticasService] registrarDerrota");
        Estadisticas stats = cargar();
        stats.partidasJugadas++;
        stats.rachaActual = 0;
        stats.diarioJugadoHoy = true;
        stats.ultimoDiaJugado = LocalDate.now().toString();
        guardar(stats);
    }

    // =========================
    // 🔧 EXTRAER STRING
    // =========================
    private static String extraerString(String json, String clave) {
        try {
            String patron = "\"" + clave + "\":";
            int inicio = json.indexOf(patron);
            if (inicio == -1) return "";
            inicio += patron.length();
            while (inicio < json.length() && json.charAt(inicio) == ' ') inicio++;
            if (inicio < json.length() && json.charAt(inicio) == '"') {
                inicio++;
                int fin = json.indexOf('"', inicio);
                return (fin == -1) ? "" : json.substring(inicio, fin).trim();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    // =========================
    // 🔧 EXTRAER ENTERO
    // =========================
    private static int extraerInt(String json, String clave) {
        try {
            String patron = "\"" + clave + "\":";
            int inicio = json.indexOf(patron);
            if (inicio == -1) return 0;
            inicio += patron.length();
            int fin = json.indexOf(",", inicio);
            if (fin == -1) fin = json.indexOf("\n", inicio);
            return Integer.parseInt(json.substring(inicio, fin).trim());
        } catch (Exception e) {
            return 0;
        }
    }

    // =========================
    // 🔧 EXTRAER ARRAY
    // =========================
    private static int[] extraerArray(String json, String clave) {
        try {
            String patron = "\"" + clave + "\":";
            int inicio = json.indexOf(patron);
            if (inicio == -1) return new int[6];
            inicio = json.indexOf("[", inicio) + 1;
            int fin = json.indexOf("]", inicio);
            String contenido = json.substring(inicio, fin);
            String[] partes  = contenido.split(",");
            int[] resultado  = new int[partes.length];
            for (int i = 0; i < partes.length; i++)
                resultado[i] = Integer.parseInt(partes[i].trim());
            return resultado;
        } catch (Exception e) {
            return new int[6];
        }
    }

    // =========================
    // 🔧 EXTRAER BOOLEANO
    // =========================
    private static boolean extraerBoolean(String json, String clave) {
        try {
            String patron = "\"" + clave + "\":";
            int inicio = json.indexOf(patron);
            if (inicio == -1) return false;
            inicio += patron.length();
            int fin = json.indexOf(",", inicio);
            if (fin == -1) fin = json.indexOf("\n", inicio);
            return Boolean.parseBoolean(json.substring(inicio, fin).trim());
        } catch (Exception e) {
            return false;
        }
    }
}