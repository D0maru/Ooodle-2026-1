package Servicios;

import ftgw.ooodle.Modelo.Usuario;

public class DAOUsuario {
    private Usuario[] cargarUsuarios(){
        return new Usuario[0];
    }
    private String agregarUsuario(){
        String r = "El usuario ha sido agregado";
        return r;
    }

    private String eliminarUsuario(int id){
        String r = "El usuario ha sido eliminado";
        return r;
    }

    private Usuario compararFechas(){
        return new Usuario();
    }
  
}
