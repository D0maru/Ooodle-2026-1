package Servicios;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class EstadisticasService {

    private static final String RUTA = obtenerRuta();

    private static String obtenerRuta() {
        try {
            // Ruta absoluta del .class de esta clase en target/classes
            String claseUrl = EstadisticasService.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath();

            // claseUrl termina en /target/classes/  →  subimos 2 niveles = raíz del proyecto
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
            return "Estadisticas.json"; // fallback junto al ejecutable
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
            stats.partidasJugadas  = extraerInt(contenido, "partidasJugadas");
            stats.partidasGanadas  = extraerInt(contenido, "partidasGanadas");
            stats.rachaActual      = extraerInt(contenido, "rachaActual");
            stats.rachaMaxima      = extraerInt(contenido, "rachaMaxima");
            stats.porcentajeGanadas = extraerInt(contenido, "porcentajeGanadas");
            stats.indiceAdivinanza = extraerArray(contenido, "indiceAdivinanza");
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
            writer.write("\"partidasJugadas\": "  + stats.partidasJugadas  + ",\n");
            writer.write("\"partidasGanadas\": "  + stats.partidasGanadas  + ",\n");
            writer.write("\"rachaActual\": "       + stats.rachaActual       + ",\n");
            writer.write("\"rachaMaxima\": "       + stats.rachaMaxima       + ",\n");
            writer.write("\"porcentajeGanadas\": " + stats.porcentajeGanadas + ",\n");
            writer.write("\"indiceAdivinanza\": [");
            for (int i = 0; i < stats.indiceAdivinanza.length; i++) {
                writer.write(String.valueOf(stats.indiceAdivinanza[i]));
                if (i < stats.indiceAdivinanza.length - 1) writer.write(", ");
            }
            writer.write("]\n}");
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
        guardar(stats);
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
            int fin   = json.indexOf("]", inicio);
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
}