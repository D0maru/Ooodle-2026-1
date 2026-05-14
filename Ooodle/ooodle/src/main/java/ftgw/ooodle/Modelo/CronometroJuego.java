package ftgw.ooodle.Modelo;

public class CronometroJuego {

    private int segundosTranscurridos = 0;
    private final int max_segundos = 3600; // 1 hora
    /** 
     * @return String
     */
    public String incrementoSegundos(){
        segundosTranscurridos++;
        return obtenerTiempoFormateado();
    }
    /** 
     * @return String
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
     * @return String
     */
    public String reiniciar(){
        segundosTranscurridos = 0;
        return obtenerTiempoFormateado();
    }
    /** 
     * @return int
     */
    public int getSegundosTranscurridos() {
        return segundosTranscurridos;
    }
    /** 
     * @return boolean
     */
    public boolean esTiempoMaximo() {
        return segundosTranscurridos >= max_segundos;
    }
}