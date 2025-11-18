package modelo;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class Heroe extends Personaje {

    private static final Scanner sc = new Scanner(System.in); // ← CORRECCIÓN
    private ArrayList<Habilidad> habilidades = new ArrayList<>();

    public Heroe(String nombre, int vidaHp, int magiaMp, int ataque, int defensa, int velocidad) {
        super(nombre, vidaHp, magiaMp, ataque, defensa, velocidad);
    }

    public void agregarHabilidad(Habilidad h) {
        habilidades.add(h);
    }

    @Override
    public void atacar(Personaje enemigo) {
        if (getEstado() != null && getEstado().getNombre().equals("Paralizado")) {
            System.out.println(getNombre() + " está paralizado y no puede atacar.");
            return;
        }

        int danio = this.getAtaque() - enemigo.getDefensa();
        if (danio < 0) danio = 0;

        enemigo.setVidaHp(enemigo.getVidaHp() - danio);
        if (enemigo.getVidaHp() <= 0) {
            enemigo.setVive(false);
            enemigo.setVidaHp(0);
            System.out.println(enemigo.getNombre() + " ha sido derrotado.");
        } else {
            System.out.println(this.getNombre() + " ataca a " + enemigo.getNombre() + " causando " + danio + " puntos de daño.");
        }
    }

    public void defender() {
        System.out.println(this.getNombre() + " se anda preparando para defenderse del próximo ataque. Defensa aumentada temporalmente.");
        setDefensa(getDefensa() + 5);
    }

    public void curar(int cantidad) {
        this.setVidaHp(this.getVidaHp() + cantidad);
        System.out.println(this.getNombre() + " se cura " + cantidad + " puntos de vida. Vida actual: " + this.getVidaHp());
    }

    public void usarHabilidad(ArrayList<Heroe> heroes, List<Enemigo> enemigos) {
        if (habilidades.isEmpty()) {
            System.out.println(getNombre() + " no tiene habilidades.");
            return;
        }

        System.out.println("\nHabilidades disponibles:");
        for (int i = 0; i < habilidades.size(); i++) {
            Habilidad h = habilidades.get(i);
            System.out.println((i + 1) + ". " + h.getNombre() + " (MP: " + h.getCosteMp() + ")");
        }

        System.out.print("Elige una habilidad: ");
        int opcion = sc.nextInt();

        if (opcion < 1 || opcion > habilidades.size()) {
            System.out.println("\nTe inventaste esa opcion.");
            return;
        }

        Habilidad h = habilidades.get(opcion - 1);

        if (getMagiaMp() < h.getCosteMp()) {
            System.out.println("\nNo tienes suficiente MP para usar " + h.getNombre() + ".");
            return;
        }

        setMagiaMp(getMagiaMp() - h.getCosteMp());
        System.out.println(getNombre() + " usa " + h.getNombre() + "!");

        switch (h.getTipo().toLowerCase()) {
            case "daño" -> {
                Enemigo enemigo = elegirEnemigo(enemigos);
                if (enemigo == null) return;
                enemigo.setVidaHp(enemigo.getVidaHp() - h.getPoder());
                System.out.println(enemigo.getNombre() + " recibe " + h.getPoder() + " puntos de daño mágico.");
                if (enemigo.getVidaHp() <= 0) enemigo.setVive(false);
            }

            case "estado" -> {
                Enemigo enemigo = elegirEnemigo(enemigos);
                if (enemigo == null) return;
                enemigo.setEstado(new Estado(h.getEstado(), h.getDuracion()));
                System.out.println(enemigo.getNombre() + " ahora está " + h.getEstado() + ".");
            }

            case "curación" -> {
                System.out.println("\n¿A qué compañero quieres curar?");
                for (int i = 0; i < heroes.size(); i++) {
                    Heroe aliado = heroes.get(i);
                    if (aliado.estaVivo()) {
                        System.out.println((i + 1) + ". " + aliado.getNombre() + " (HP: " + aliado.getVidaHp() + ")");
                    }
                }
                System.out.print("Elige número: ");
                int eleccion = sc.nextInt();

                if (eleccion < 1 || eleccion > heroes.size() || !heroes.get(eleccion - 1).estaVivo()) {
                    System.out.println("\nY esa opción de dónde salió? Pierdes el turno por inventarte cosas.");
                    return;
                }

                Heroe aliadoCurado = heroes.get(eleccion - 1);
                aliadoCurado.setVidaHp(aliadoCurado.getVidaHp() + h.getPoder());
                System.out.println(aliadoCurado.getNombre() + " recupera " + h.getPoder() + " puntos de vida.");
            }

            default -> System.out.println("\n¿Qué es esa habilidad? Para próximas actualizaciones te la ponemos.");
        }
    }

    private Enemigo elegirEnemigo(List<Enemigo> enemigos) {
        List<Enemigo> vivos = new ArrayList<>();
        for (Enemigo e : enemigos) if (e.estaVivo()) vivos.add(e);

        if (vivos.isEmpty()) return null;

        System.out.println("\nElige un enemigo:");
        for (int i = 0; i < vivos.size(); i++) {
            Enemigo e = vivos.get(i);
            System.out.println((i + 1) + ". " + e.getNombre() + " (HP: " + e.getVidaHp() + ")");
        }

        System.out.print("Número del enemigo: ");
        int eleccion = sc.nextInt();

        if (eleccion < 1 || eleccion > vivos.size()) {
            System.out.println("Opción inválida, se elige uno al azar.");
            return vivos.get(new Random().nextInt(vivos.size()));
        }

        return vivos.get(eleccion - 1);
    }
}
