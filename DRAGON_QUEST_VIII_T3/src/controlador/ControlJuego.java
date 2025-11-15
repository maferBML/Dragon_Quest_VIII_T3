package controlador;

import modelo.*;
import vista.*;


public class ControlJuego {

    private Combate combate;
    private VistaJuegoTerminal vista;

    public ControlJuego(Combate combate, VistaJuegoTerminal vista) {
        this.combate = combate;
        this.vista = vista;
    }

    public void iniciar() {
        vista.mostrarInicioCombate();
        int turno = 1;

        while (combate.hayVivosHeroes() && combate.hayVivosEnemigos()) {
            vista.mostrarTurno(turno);
            vista.mostrarEstado(combate.getHeroes(), combate.getEnemigos());

            for (Personaje p : combate.ordenarPorVelocidad()) {
                if (!p.estaVivo()) continue;

                combate.aplicarEfectos(p);
                if (!p.estaVivo()) continue;

                if (p instanceof Heroe) {
                    Heroe h = (Heroe) p;
                    Enemigo objetivo = combate.elegirEnemigo();
                    if (objetivo == null) break;

                    int opcion = vista.pedirAccion(h);
                    combate.ejecutarAccion(h, opcion, objetivo);

                } else if (p instanceof Enemigo) {
                    Heroe objetivo = combate.elegirHeroe();
                    if (objetivo != null) ((Enemigo) p).accionAutomatica(objetivo);
                }

                if (!combate.hayVivosHeroes() || !combate.hayVivosEnemigos()) break;
            }

            turno++;
        }

        if (combate.hayVivosHeroes()) vista.mostrarVictoriaHeroes();
        else vista.mostrarVictoriaEnemigos();
    }
}
