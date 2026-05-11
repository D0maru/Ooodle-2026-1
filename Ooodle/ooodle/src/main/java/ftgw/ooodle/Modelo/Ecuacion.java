package ftgw.ooodle.Modelo;

public class Ecuacion {

    private int Evaluar(int a, int b, int c, int d) {
        return (a * b) + c - d;
    }

    public int[] GenerarEcuacion(int target, boolean modoDificil) {
        int min = 1;
        int max;
        if (modoDificil) {
            max = 12;
        } else {
            max = 9;
        }
        for (int a = min; a <= max; a++) {
            for (int b = min; b <= max; b++) {
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
