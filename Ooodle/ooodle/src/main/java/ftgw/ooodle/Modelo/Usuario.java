package ftgw.ooodle.Modelo;

public class Usuario {
    public int id;
    private String nickname;
    private boolean juegoDiario;
    
    public Usuario(int id, String nickname, boolean juegoDiario) {
        this.id = id;
        this.nickname = nickname;
        this.juegoDiario = juegoDiario;
    }
}
