package ftgw.ooodle.Modelo;
/**
 * Cronómetro para controlar el tiempo transcurrido en una partida.
 * Permite incrementar, consultar y reiniciar el tiempo,
 * con un límite máximo de 1 hora (3600 segundos).
 */
public class CronometroJuego {
    /** Cantidad de segundos transcurridos desde el inicio o último reinicio. */
    private int segundosTranscurridos = 0;
    /** Límite máximo de segundos permitidos (3600 = 1 hora). */
    private final int max_segundos = 3600; 
    /**
     * Incrementa en uno los segundos transcurridos.
     * @return El tiempo actual formateado como "MM:SS" o mensaje de tiempo excedido.
     */
    public String incrementoSegundos(){
        segundosTranscurridos++;
        return obtenerTiempoFormateado();
    }
    /**
     * Devuelve el tiempo transcurrido en formato "MM:SS".
     * Si se alcanzó el límite máximo, retorna un mensaje de aviso.
     * @return Tiempo formateado o mensaje "Te demoraste mucho!".
     */
    public String obtenerTiempoFormateado(){
        if(segundosTranscurridos >= max_segundos){
            return "Te demoraste mucho!";
        }
        int minutos = segundosTranscurridos / 60;
        int segundos=segundosTranscurridos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
    /**
     * Reinicia el cronómetro a cero.
     * @return El tiempo formateado tras el reinicio, es decir "00:00".
     */
    public String reiniciar(){
        segundosTranscurridos = 0;
        return obtenerTiempoFormateado();
    }
    /**
     * Obtiene los segundos totales transcurridos.
     * @return Número entero de segundos desde el inicio o último reinicio.
     */
    public int getSegundosTranscurridos() {
        return segundosTranscurridos;
    }
    /**
     * Verifica si se ha alcanzado o superado el tiempo máximo de juego.
     * @return { true} si el tiempo llegó al límite; {false} en caso contrario.
     */ 
    public boolean esTiempoMaximo() {
        return segundosTranscurridos >= max_segundos;
    }
}