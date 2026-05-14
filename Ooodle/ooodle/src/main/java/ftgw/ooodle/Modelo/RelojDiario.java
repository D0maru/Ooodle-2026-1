package ftgw.ooodle.Modelo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
/**
 * Controla el acceso diario al juego mediante un reloj.
 * Calcula el tiempo restante hasta la medianoche y determina
 * si el usuario puede iniciar una nueva partida.
 */
public class RelojDiario {
    /** Indica si el usuario tenía permiso para jugar al momento de crear esta instancia. */
    private final boolean puedeJugarInicial;
    /**
     * Obtiene el estado inicial de permiso de juego.
     * @return true si el usuario podía jugar al crear la instancia; false en caso contrario.
     */
    public boolean isPuedeJugarInicial() {
        return puedeJugarInicial;
    }
    /**
     * Crea el reloj diario con el estado de permiso inicial del usuario.
     * @param puedeJugarInicial true si el usuario puede jugar desde el inicio.
     */
    public RelojDiario(boolean puedeJugarInicial) {
        this.puedeJugarInicial = puedeJugarInicial;
    }
    /**
     * Calcula el tiempo restante hasta la medianoche.
     * @return Cadena con formato "HH:MM:SS" representando el tiempo restante,
     *         o "00:00:00" si ya se llegó o superó la medianoche.
     */
    public String getTiempoRestante() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime medianoche = ahora.toLocalDate().plusDays(1).atStartOfDay();
        long seg = ChronoUnit.SECONDS.between(ahora, medianoche);

        if (seg <= 0) return "00:00:00";

        return String.format("%02d:%02d:%02d", seg / 3600, (seg % 3600) / 60, seg % 60);
    }
    /**
     * Determina si el usuario puede iniciar una partida en este momento.
     * Permite jugar si tenía permiso inicial o si ya pasó la medianoche.
     * @return true si el usuario puede jugar; false en caso contrario.
     */
    public boolean puedeJugar() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime medianoche = ahora.toLocalDate().plusDays(1).atStartOfDay();
        return puedeJugarInicial || ChronoUnit.SECONDS.between(ahora, medianoche) <= 0;
    }
}