package ftgw.ooodle.Modelo;
public class Usuario {
    public int id;
    private String nickname;
    private boolean juegoDiario;
    private boolean puedeJugar;
    
    /** 
     * @return boolean
     */
    public boolean isJuegoDiario() {
        return juegoDiario;
    }
    /** 
     * @param juegoDiario
     * @return boolean
     */
    public boolean setJuegoDiario(boolean juegoDiario) {
        this.juegoDiario = juegoDiario;
        return this.juegoDiario == juegoDiario;
    }
    public Usuario(int id, String nickname, Boolean juegoDiario) {
        this.id = id;
        this.nickname = nickname;
        this.juegoDiario = juegoDiario;
    }
    /** 
     * @return String
     */
    public String getNickname() {
        return nickname;
    }
    /** 
     * @return int
     */
    public int getId(){
        return id;
    }
    /** 
     * @param puedeJugar
     * @return boolean
     */
    public boolean setPuedeJugar(boolean puedeJugar) {
        this.puedeJugar = puedeJugar;
        return this.puedeJugar;
    }
    /** 
     * @return boolean
     */
    //Getter para permiso de jugar
    public boolean isPuedeJugar() {
        return puedeJugar;
    }
}
