package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class Heroe extends Personaje {

    private ArrayList<Habilidad> habilidades = new ArrayList<>();

    public Heroe(String nombre, int vidaHp, int magiaMp, int ataque, int defensa, int velocidad) {
        super(nombre, vidaHp, magiaMp, ataque, defensa, velocidad);
    }

    public void agregarHabilidad(Habilidad h) {
        habilidades.add(h);
    }

    // ==== GETTER PARA LA GUI ====
    public ArrayList<Habilidad> getHabilidades() {
        return habilidades;
    }

    // ==== ATAQUE COMPARTIDO (TEXTO) ====
    public String atacarTexto(Personaje enemigo) {
        StringBuilder sb = new StringBuilder();

        if (getEstado() != null && getEstado().getNombre().equals("Paralizado")) {
            sb.append(getNombre()).append(" está paralizado y no puede atacar.\n");
            return sb.toString();
        }

        int danio = this.getAtaque() - enemigo.getDefensa();
        if (danio < 0) danio = 0;

        enemigo.setVidaHp(enemigo.getVidaHp() - danio);
        if (enemigo.getVidaHp() <= 0) {
            enemigo.setVive(false);
            enemigo.setVidaHp(0);
            sb.append(enemigo.getNombre()).append(" ha sido derrotado.\n");
        } else {
            sb.append(this.getNombre()).append(" ataca a ")
              .append(enemigo.getNombre())
              .append(" causando ").append(danio)
              .append(" puntos de daño.\n");
        }
        return sb.toString();
    }

    @Override
    public void atacar(Personaje enemigo) {
        // Versión consola: imprime el texto
        System.out.print(atacarTexto(enemigo));
    }

    // ==== DEFENSA COMPARTIDA ====
    public String defenderTexto() {
        setDefensa(getDefensa() + 5);
        return this.getNombre()
                + " se anda preparando para defenderse del próximo ataque. Defensa aumentada temporalmente.\n";
    }

    public void defender() {
        System.out.print(defenderTexto());
    }

    public void curar(int cantidad) {
        this.setVidaHp(this.getVidaHp() + cantidad);
        System.out.println(this.getNombre() + " se cura " + cantidad + " puntos de vida. Vida actual: " + this.getVidaHp());
    }

    // ================== LÓGICA DE HABILIDADES COMPARTIDA ==================

    private String aplicarHabilidad(Habilidad h,
                                    ArrayList<Heroe> heroes,
                                    Heroe objetivoHeroe,
                                    Enemigo objetivoEnemigo) {

        StringBuilder sb = new StringBuilder();
        String tipo = h.getTipo().toLowerCase();

        switch (tipo) {
            case "daño" -> {
                if (objetivoEnemigo == null || !objetivoEnemigo.estaVivo()) {
                    sb.append("El enemigo objetivo no es válido.\n");
                    break;
                }
                objetivoEnemigo.setVidaHp(objetivoEnemigo.getVidaHp() - h.getPoder());
                sb.append(objetivoEnemigo.getNombre())
                  .append(" recibe ").append(h.getPoder())
                  .append(" puntos de daño mágico.\n");
                if (objetivoEnemigo.getVidaHp() <= 0) {
                    objetivoEnemigo.setVive(false);
                    objetivoEnemigo.setVidaHp(0);
                    sb.append(objetivoEnemigo.getNombre()).append(" ha sido derrotado.\n");
                }
            }

            case "estado" -> {
                if (objetivoEnemigo == null || !objetivoEnemigo.estaVivo()) {
                    sb.append("El enemigo objetivo no es válido.\n");
                    break;
                }
                objetivoEnemigo.setEstado(new Estado(h.getEstado(), h.getDuracion()));
                sb.append(objetivoEnemigo.getNombre())
                  .append(" ahora está ").append(h.getEstado()).append(".\n");
            }

            case "curación" -> {
                if (objetivoHeroe == null || !objetivoHeroe.estaVivo()) {
                    sb.append("El aliado objetivo no es válido.\n");
                    break;
                }
                objetivoHeroe.setVidaHp(objetivoHeroe.getVidaHp() + h.getPoder());
                sb.append(objetivoHeroe.getNombre())
                  .append(" recupera ").append(h.getPoder())
                  .append(" puntos de vida.\n");
            }

            default -> sb.append("¿Qué es esa habilidad? Para próximas actualizaciones te la ponemos.\n");
        }

        return sb.toString();
    }

    // ==== VERSIÓN CONSOLA ====
    public void usarHabilidad(ArrayList<Heroe> heroes, List<Enemigo> enemigos) {
        if (habilidades.isEmpty()) {
            System.out.println(getNombre() + " no tiene habilidades.");
            return;
        }

        Scanner sc = new Scanner(System.in);

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

        String tipo = h.getTipo().toLowerCase();

        Enemigo objetivoEnemigo = null;
        Heroe objetivoHeroe = null;

        if (tipo.equals("daño") || tipo.equals("estado")) {
            objetivoEnemigo = elegirEnemigo(enemigos);
            if (objetivoEnemigo == null) return;
        } else if (tipo.equals("curación")) {
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

            objetivoHeroe = heroes.get(eleccion - 1);
        }

        String texto = aplicarHabilidad(h, heroes, objetivoHeroe, objetivoEnemigo);
        System.out.print(texto);
    }

    // ==== VERSIÓN GUI ====
    public String usarHabilidadGUI(Habilidad h,
                                   ArrayList<Heroe> heroes,
                                   Heroe objetivoHeroe,
                                   Enemigo objetivoEnemigo) {

        StringBuilder sb = new StringBuilder();

        if (habilidades.isEmpty()) {
            sb.append(getNombre()).append(" no tiene habilidades.\n");
            return sb.toString();
        }

        if (!habilidades.contains(h)) {
            sb.append("Esa habilidad no pertenece a ").append(getNombre()).append(".\n");
            return sb.toString();
        }

        if (getMagiaMp() < h.getCosteMp()) {
            sb.append("No tienes suficiente MP para usar ").append(h.getNombre()).append(".\n");
            return sb.toString();
        }

        setMagiaMp(getMagiaMp() - h.getCosteMp());
        sb.append(getNombre()).append(" usa ").append(h.getNombre()).append("!\n");
        sb.append(aplicarHabilidad(h, heroes, objetivoHeroe, objetivoEnemigo));

        return sb.toString();
    }

    // ====== auxiliares ======
    private Enemigo elegirEnemigo(List<Enemigo> enemigos) {
        Scanner sc = new Scanner(System.in);
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
