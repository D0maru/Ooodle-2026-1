package ftgw.ooodle;
public class GenerarEcuacion {

    private static int evaluate(int a, int b, int c, int d) {
        return (a * b) + c - d;
    }
    public static int[] generateEquation(int target, boolean modoDificil) { //retorna un arreglo [a,b,c,d]
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

                        if (evaluate(a, b, c, d) == target) {
                            return new int[]{a, b, c, d};
                        }
                    }
                }
            }
        }
        return null;
    }
    public static void main(String[] args) {
        int target = 0;
        boolean modoDificil = false; // false = 1-9, true = 1-12
        if(modoDificil){
            target = (int)(Math.random() * 149) - 7;
        }else{
            target = (int)(Math.random() * 83) - 4;
        }
        //int[] eq = generateEquation(target, modoDificil);
    }
}