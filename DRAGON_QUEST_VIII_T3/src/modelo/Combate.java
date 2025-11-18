package modelo;

//import modelo.*;
import java.util.*;

public class Combate {
    private List<Heroe> heroes;
    private List<Enemigo> enemigos;
    private Random random = new Random();

    public Combate(List<Heroe> heroes, List<Enemigo> enemigos) {
        this.heroes = heroes;
        this.enemigos = enemigos;
    }

    public boolean hayVivosHeroes() {
        return hayVivos(heroes);
    }

    public boolean hayVivosEnemigos() {
        return hayVivos(enemigos);
    }

    private boolean hayVivos(List<? extends Personaje> lista) {
        for (Personaje p : lista) if (p.estaVivo()) return true;
        return false;
    }

    public List<Personaje> ordenarPorVelocidad() {
        List<Personaje> participantes = new ArrayList<>();
        participantes.addAll(heroes);
        participantes.addAll(enemigos);

        participantes.sort((a, b) -> b.getVelocidad() - a.getVelocidad());
        return participantes;
    }

    public void aplicarEfectos(Personaje p) {
        if (p.getEstado() != null) {
            p.getEstado().aplicarEfecto(p);
            if (p.getEstado().terminado()) {
                p.setEstado(null);
            }
        }
    }

    public void ejecutarAccion(Heroe h, int opcion, Enemigo objetivo) {
        switch (opcion) {
            case 1 -> h.atacar(objetivo);
            case 2 -> h.defender();
            case 3 -> h.usarHabilidad(new ArrayList<>(heroes), enemigos);  // ← CORRECCIÓN
        }
    }

    public Enemigo elegirEnemigo() {
        List<Enemigo> vivos = new ArrayList<>();
        for (Enemigo e : enemigos) if (e.estaVivo()) vivos.add(e);
        return vivos.isEmpty() ? null : vivos.get(random.nextInt(vivos.size()));
    }

    public Heroe elegirHeroe() {
        List<Heroe> vivos = new ArrayList<>();
        for (Heroe h : heroes) if (h.estaVivo()) vivos.add(h);
        return vivos.isEmpty() ? null : vivos.get(random.nextInt(vivos.size()));
    }

    public List<Heroe> getHeroes() { return heroes; }
    public List<Enemigo> getEnemigos() { return enemigos; }
}
