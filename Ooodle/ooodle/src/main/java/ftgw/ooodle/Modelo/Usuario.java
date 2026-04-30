package ftgw.ooodle.Modelo;

import java.sql.Date;

public class Usuario {
    public int id;
    private String nickname;
    private boolean juegoDiario;
    
    public Usuario(int id, String nickname, Boolean juegoDiario) {
        this.id = id;
        this.nickname = nickname;
        this.juegoDiario = juegoDiario;
    }
    public String getNickname() {
        return nickname;
    }
}
