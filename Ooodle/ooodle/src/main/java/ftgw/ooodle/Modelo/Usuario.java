package ftgw.ooodle.Modelo;
public class Usuario {
    public int id;
    private String nickname;
    private boolean juegoDiario;
    private boolean puedeJugar;
    
    public boolean isJuegoDiario() {
        return juegoDiario;
    }
    public boolean setJuegoDiario(boolean juegoDiario) {
        this.juegoDiario = juegoDiario;
        return this.juegoDiario == juegoDiario;
    }
    public Usuario(int id, String nickname, Boolean juegoDiario) {
        this.id = id;
        this.nickname = nickname;
        this.juegoDiario = juegoDiario;
    }
    public String getNickname() {
        return nickname;
    }
    public int getId(){
        return id;
    }
    public boolean setPuedeJugar(boolean puedeJugar) {
        this.puedeJugar = puedeJugar;
        return this.puedeJugar;
    }
    //Getter para permiso de jugar
    public boolean isPuedeJugar() {
        return puedeJugar;
    }
}
