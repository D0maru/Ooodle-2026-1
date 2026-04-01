package Servicios;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class EstadisticasService {

    private static final String RUTA =
        "C:\\Users\\mbgom\\OneDrive\\Desktop\\Repositorio Ooodle\\Ooodle-2026-1\\Ooodle\\ooodle\\src\\main\\resources\\Servicios\\Estadisticas.json";

    // =========================
    // 📥 CARGAR
    // =========================
    public static Estadisticas cargar() {

        Estadisticas stats = new Estadisticas();

        try {
            File archivo = new File(RUTA);

            if (!archivo.exists()) {
                return stats;
            }

            String contenido = new String(Files.readAllBytes(archivo.toPath()));

            stats.partidasJugadas = extraerInt(contenido, "partidasJugadas");
            stats.partidasGanadas = extraerInt(contenido, "partidasGanadas");
            stats.rachaActual = extraerInt(contenido, "rachaActual");
            stats.rachaMaxima = extraerInt(contenido, "rachaMaxima");

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

        try {
            File archivo = new File(RUTA);

            // Crear carpetas si no existen
            archivo.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(archivo);

            writer.write("{\n");
            writer.write("\"partidasJugadas\": " + stats.partidasJugadas + ",\n");
            writer.write("\"partidasGanadas\": " + stats.partidasGanadas + ",\n");
            writer.write("\"rachaActual\": " + stats.rachaActual + ",\n");
            writer.write("\"rachaMaxima\": " + stats.rachaMaxima + ",\n");

            writer.write("\"indiceAdivinanza\": [");

            for (int i = 0; i < stats.indiceAdivinanza.length; i++) {
                writer.write(String.valueOf(stats.indiceAdivinanza[i]));
                if (i < stats.indiceAdivinanza.length - 1) {
                    writer.write(", ");
                }
            }

            writer.write("]\n");
            writer.write("}");

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
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
            if (fin == -1) {
                fin = json.indexOf("\n", inicio);
            }

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
            String[] partes = contenido.split(",");

            int[] resultado = new int[partes.length];

            for (int i = 0; i < partes.length; i++) {
                resultado[i] = Integer.parseInt(partes[i].trim());
            }

            return resultado;

        } catch (Exception e) {
            return new int[6];
        }
    }
}