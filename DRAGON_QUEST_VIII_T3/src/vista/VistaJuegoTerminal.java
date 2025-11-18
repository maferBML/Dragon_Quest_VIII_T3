package vista;

import modelo.*;


import java.util.*;

public class VistaJuegoTerminal {
    private Scanner sc = new Scanner(System.in);

    public void mostrarInicioCombate() {
        System.out.println("\n=== ¡Comienza el combate! ===\n");
    }

    public void mostrarTurno(int turno) {
        System.out.println("---- Turno " + turno + " ----");
    }

    public int pedirAccion(Heroe h) {
        System.out.println("\n==============================");
        System.out.println("     Turno de " + h.getNombre());
        System.out.println("==============================");
        System.out.println("1. Atacar");
        System.out.println("2. Defender");
        System.out.println("3. Usar Habilidad");
        System.out.print("Elige una acción: ");
        return sc.nextInt();
    }

    public void mostrarEstado(List<Heroe> heroes, List<Enemigo> enemigos) {

        System.out.println("Héroes:");
        for (Heroe h : heroes) {
            System.out.println("  " + h.getNombre() + " - HP: " + h.getVidaHp() + 
                " MP: " + h.getMagiaMp() + estadoString(h.getEstado()));
        }

        System.out.println("Enemigos:");
        for (Enemigo e : enemigos) {
            System.out.println("  " + e.getNombre() + " - HP: " + e.getVidaHp() +
                estadoString(e.getEstado()));
        }
    }

    private String estadoString(Estado est) {
        if (est == null) return "";
        return " [" + est.getNombre() + " (" + est.getDuracion() + ")]";
    }

    public void mostrarMensaje(String msg) {
        System.out.println(msg);
    }

    public void mostrarVictoriaHeroes() {
        System.out.println("¡Los héroes han ganado!");
    }

    public void mostrarVictoriaEnemigos() {
        System.out.println("¡Los enemigos han ganado!");
    }
}
