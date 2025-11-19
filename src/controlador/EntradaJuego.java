package controlador;

public class EntradaJuego {

    private ControlJuego control;

    public EntradaJuego() {
        this.control = new ControlJuego();
    }

    public void iniciar() {
        // Por ahora solo modo consola
        control.iniciarConsola();
    }
}
