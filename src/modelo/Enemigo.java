package modelo;

import java.util.Random;

public class Enemigo extends Personaje {
    private String tipo;
    private boolean miniJefe;
    private Random random = new Random();

    public Enemigo(String nombre, int vidaHp, int magiaMp, int ataque, int defensa, int velocidad, String tipo) {
        super(nombre, vidaHp, magiaMp, ataque, defensa, velocidad);
        this.tipo = tipo;
    }

    public Enemigo(String nombre, int vidaHp, int magiaMp, int ataque, int defensa, int velocidad, String tipo, boolean miniJefe) {
        super(nombre, vidaHp, magiaMp, ataque, defensa, velocidad);
        this.tipo = tipo;
        this.miniJefe = miniJefe;
        if (miniJefe) {
            setVidaHp(getVidaHp() + 50);
            setAtaque(getAtaque() + 15);
            setDefensa(getDefensa() + 10);
        }
    }

    // ==== ATAQUE COMPARTIDO (texto) ====
    public String atacarTexto(Personaje enemigo) {
        StringBuilder sb = new StringBuilder();

        if (getEstado() != null && getEstado().getNombre().equals("Paralizado")) {
            sb.append(getNombre()).append(" está paralizado y no puede atacar.\n");
            return sb.toString();
        }

        boolean critico = false;
        int danioBase = this.getAtaque() - enemigo.getDefensa();
        if (danioBase < 0) danioBase = 0;

        if (miniJefe && random.nextInt(100) < 40) {
            critico = true;
            danioBase *= 2;
            sb.append(getNombre()).append(" te da un GOLPE CRÍTICO! CORRE!\n");
        }

        enemigo.setVidaHp(enemigo.getVidaHp() - danioBase);
        sb.append(getNombre()).append(" ataca a ").append(enemigo.getNombre())
          .append(" causando ").append(danioBase).append(" puntos de daño.\n");

        if (miniJefe && critico && enemigo.estaVivo()) {
            if (random.nextInt(100) < 70) {
                int duracionSueño = random.nextInt(3) + 1;
                enemigo.setEstado(new Estado("Sueño", duracionSueño));
                sb.append(enemigo.getNombre())
                  .append(" ha caído dormido por ")
                  .append(duracionSueño).append(" turnos!\n");
            }
        }

        if (enemigo.getVidaHp() <= 0) {
            enemigo.setVive(false);
            enemigo.setVidaHp(0);
            sb.append(enemigo.getNombre()).append(" ha sido derrotado.\n");
        }

        return sb.toString();
    }

    @Override
    public void atacar(Personaje enemigo) {
        System.out.print(atacarTexto(enemigo));
    }

    public String defenderTexto() {
        setDefensa(getDefensa() + 10);
        return this.getNombre() + " se prepara para defenderse.\n";
    }

    public void defender() {
        System.out.print(defenderTexto());
    }

    // ==== IA COMPARTIDA ====
    public String accionAutomaticaTexto(Personaje enemigo) {
        StringBuilder sb = new StringBuilder();

        if (getEstado() != null && getEstado().getNombre().equals("Paralizado")) {
            sb.append(getNombre()).append(" está paralizado y no puede actuar.\n");
            return sb.toString();
        }

        int decision = random.nextInt(100);

        if (tipo.equalsIgnoreCase("agresivo")) {
            sb.append(atacarTexto(enemigo));
        } else if (tipo.equalsIgnoreCase("defensivo") && decision < 30) {
            sb.append(defenderTexto());
        } else {
            sb.append(atacarTexto(enemigo));
        }

        return sb.toString();
    }

    public void accionAutomatica(Personaje enemigo) {
        System.out.print(accionAutomaticaTexto(enemigo));
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean esMiniJefe() { return miniJefe; }
}
