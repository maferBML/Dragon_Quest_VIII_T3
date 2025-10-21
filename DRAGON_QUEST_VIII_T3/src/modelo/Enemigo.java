package modelo;
import java.util.Random;

public class Enemigo extends Personaje {
    private String tipo;
    private Random random = new Random();

    public Enemigo(String nombre, int vidaHp, int magiaMp, int ataque, int defensa, int velocidad, String tipo) {
        super(nombre, vidaHp, magiaMp, ataque, defensa, velocidad);
        this.tipo = tipo;
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

    public String getTipo() {
         return tipo; 
        }
    public void setTipo(String tipo) { this.tipo = tipo; }

    private boolean miniJefe;

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

    public boolean esMiniJefe() {
        return miniJefe;
}

}

