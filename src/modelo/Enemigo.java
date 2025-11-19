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

    @Override
    public void atacar(Personaje enemigo) {
        if (getEstado() != null && getEstado().getNombre().equals("Paralizado")) {
            System.out.println(getNombre() + " está paralizado y no puede atacar. (lero lero)");
            return;
        }
        boolean critico = false;
        int danioBase = this.getAtaque() - enemigo.getDefensa();
        if (danioBase < 0) danioBase = 0;

        if (miniJefe && random.nextInt(100) < 40) {
            critico = true;
            danioBase *= 2;
            System.out.println(getNombre() + " te da un GOLPE CRÍTICO! CORRE!");
        }

        enemigo.setVidaHp(enemigo.getVidaHp() - danioBase);
        System.out.println(getNombre() + " ataca a " + enemigo.getNombre() + " causando " + danioBase + " puntos de daño.");

        if (miniJefe && critico && enemigo.estaVivo()) {
            if (random.nextInt(100) < 70) {
                int duracionSueño = random.nextInt(3) + 1; // 1-3 turnos
                enemigo.setEstado(new Estado("Sueño", duracionSueño));
                System.out.println(enemigo.getNombre() + " ha caído dormido por " + duracionSueño + " turnos!");
            }
        }

        if (enemigo.getVidaHp() <= 0) {
            enemigo.setVive(false);
            enemigo.setVidaHp(0);
            System.out.println(enemigo.getNombre() + " ha sido derrotado.");
        }
    }


    public void accionAutomatica(Personaje enemigo) {
        int decision = random.nextInt(100);
        if (getEstado() != null && getEstado().getNombre().equals("Paralizado")) {
            System.out.println(getNombre() + " está paralizado y no puede actuar.");
            return;
        }

        if (tipo.equalsIgnoreCase("agresivo")) {
            atacar(enemigo);
        } else if (tipo.equalsIgnoreCase("defensivo") && decision < 30) {
            defender();
        } else {
            atacar(enemigo);
        }
    }

    public void defender() {
        System.out.println(this.getNombre() + " se prepara para defenderse del próximo ataque.");
        setDefensa(getDefensa() + 10);
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean esMiniJefe() { return miniJefe; }
}