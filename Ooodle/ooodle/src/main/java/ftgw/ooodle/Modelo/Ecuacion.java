package ftgw.ooodle.Modelo;
/**
 * Clase encargada de generar y evaluar ecuaciones matemáticas
 * de la forma (a * b) + c - d = target,
 * con valores únicos entre sí según el modo de dificultad.
 */
public class Ecuacion {

    /**
     * Evalúa la expresión matemática (a * b) + c - d.
     * @param a Primer operando (multiplicando).
     * @param b Segundo operando (multiplicador).
     * @param c Valor a sumar al producto.
     * @param d Valor a restar al resultado.
     * @return Resultado entero de la expresión (a * b) + c - d.
     */
    private int Evaluar(int a, int b, int c, int d) {
        return (a * b) + c - d;
    }
    /**
     * Busca cuatro valores distintos (a, b, c, d) cuya expresión
     * (a * b) + c - d sea igual al objetivo dado.
     * @param target Valor objetivo que debe producir la ecuación.
     * @param modoDificil Si es {@code true}, los valores van de 1 a 12;
     *                    si es {@code false}, van de 1 a 9.
     * @return Arreglo {@code int[]{a, b, c, d}} con la solución encontrada,
     *         o {@code null} si no existe ninguna combinación válida.
     */
    public int[] GenerarEcuacion(int target, boolean modoDificil) {
        // Valor mínimo permitido para los operandos
        int min = 1;
        // Valor máximo según la dificultad: 12 en modo difícil, 9 en modo normal
        int max;
        if (modoDificil) {
            max = 12;
        } else {
            max = 9;
        }
        for (int a = min; a <= max; a++) {
            for (int b = min; b <= max; b++) {
                // Se omiten valores repetidos para garantizar que a, b, c y d sean distintos entre sí
                if (b == a) continue;

                for (int c = min; c <= max; c++) {
                    if (c == a || c == b) continue;

                    for (int d = min; d <= max; d++) {
                        if (d == a || d == b || d == c) continue;

                        if (Evaluar(a, b, c, d) == target) {
                            return new int[]{a, b, c, d};
                        }
                    }
                }
            }
        }
        return null;
    }
}
