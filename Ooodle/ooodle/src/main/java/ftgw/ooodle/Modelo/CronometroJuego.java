package ftgw.ooodle.Modelo;

public class CronometroJuego {

    private int segundosTranscurridos = 0;
    private final int max_segundos = 3600; // 1 hora
    public String incrementoSegundos(){
        segundosTranscurridos++;
        return obtenerTiempoFormateado();
    }
    public String obtenerTiempoFormateado(){
        if(segundosTranscurridos >= max_segundos){
            return "Te demoraste mucho!";
        }
        int minutos = segundosTranscurridos / 60;
        int segundos=segundosTranscurridos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
    public String reiniciar(){
        segundosTranscurridos = 0;
        return obtenerTiempoFormateado();
    }
    public int getSegundosTranscurridos() {
        return segundosTranscurridos;
    }
    public boolean esTiempoMaximo() {
        return segundosTranscurridos >= max_segundos;
    }
}