package ftgw.ooodle.Modelo;

public class ResultadoFila {

    public final String   estado;
    public final String   mensajeError;
    public final String[] estilosFila;
    public final int      filaValidada;

    /** Constructor para casos de error de validacion. */
    public ResultadoFila(String mensajeError) {
        this.estado       = "ERROR";
        this.mensajeError = mensajeError;
        this.estilosFila  = null;
        this.filaValidada = -1;
    }

    /** Constructor para casos de avance normal o fin de partida. */
    public ResultadoFila(String estado, String[] estilosFila, int filaValidada) {
        this.estado       = estado;
        this.mensajeError = null;
        this.estilosFila  = estilosFila;
        this.filaValidada = filaValidada;
    }
}