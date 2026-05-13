package ftgw.ooodle.Modelo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RelojDiario {

    private final boolean puedeJugarInicial;

    public RelojDiario(boolean puedeJugarInicial) {
        this.puedeJugarInicial = puedeJugarInicial;
    }

    public String getTiempoRestante() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime medianoche = ahora.toLocalDate().plusDays(1).atStartOfDay();
        long seg = ChronoUnit.SECONDS.between(ahora, medianoche);

        if (seg <= 0) return "00:00:00";

        return String.format("%02d:%02d:%02d", seg / 3600, (seg % 3600) / 60, seg % 60);
    }

    public boolean puedeJugar() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime medianoche = ahora.toLocalDate().plusDays(1).atStartOfDay();
        return puedeJugarInicial || ChronoUnit.SECONDS.between(ahora, medianoche) <= 0;
    }
}