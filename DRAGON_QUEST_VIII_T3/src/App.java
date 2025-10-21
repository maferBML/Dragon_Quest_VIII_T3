
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import controlador.Combate;
import modelo.Enemigo;
import modelo.Habilidad;
import modelo.Heroe;
public class App {
    public static void main(String[] args) {
        Heroe heroe1 = new Heroe("Héroe", 100, 30, 25, 10, 15);
        Heroe heroe2 = new Heroe("Yangus", 120, 20, 27, 12, 12);
        Heroe heroe3 = new Heroe("Jessica", 90, 50, 20, 8, 18);
        Heroe heroe4 = new Heroe("Angelo", 85, 40, 24, 9, 16);

        heroe3.agregarHabilidad(new Habilidad("Fuego", "daño", 25, 10));
        heroe3.agregarHabilidad(new Habilidad("Curar", "curación", 30, 8));
        heroe3.agregarHabilidad(new Habilidad("Veneno", "estado", 0, 6, 2, "Envenenado"));

        heroe4.agregarHabilidad(new Habilidad("Rayo Divino", "daño", 35, 12));
        heroe4.agregarHabilidad(new Habilidad("Curación Menor", "curación", 20, 6));

        ArrayList<Heroe> heroes = new ArrayList<>();
        heroes.add(heroe1);
        heroes.add(heroe2);
        heroes.add(heroe3);
        heroes.add(heroe4);

        //enemigos
        Enemigo[] enemigosArr = {
            new Enemigo("Goblin", 70, 0, 20, 8, 10, "agresivo"),
            new Enemigo("Slime", 60, 0, 15, 5, 8, "agresivo"),
            new Enemigo("Dragón", 110, 20, 30, 15, 14, "defensivo"),
            new Enemigo("Esqueleto", 80, 0, 18, 9, 13, "agresivo")
        };

        Random random = new Random();
        int indiceMiniJefe = random.nextInt(enemigosArr.length);

        Enemigo elegido = enemigosArr[indiceMiniJefe];
        enemigosArr[indiceMiniJefe] = new Enemigo(
            elegido.getNombre(), 
            elegido.getVidaHp(),
            elegido.getMagiaMp(), 
            elegido.getAtaque(), 
            elegido.getDefensa(),
            elegido.getVelocidad(), 
            elegido.getTipo(), 
            true 
        );

        System.out.println("\n¡Un " + enemigosArr[indiceMiniJefe].getNombre().toUpperCase() + 
                           " ha aparecido como JEFE (Cagaste)!");
        System.out.println("HP aumentado: " + enemigosArr[indiceMiniJefe].getVidaHp());
        System.out.println("Ataque aumentado: " + enemigosArr[indiceMiniJefe].getAtaque());
        System.out.println("Defensa aumentada: " + enemigosArr[indiceMiniJefe].getDefensa());
        System.out.println("==============================================\n");

        ArrayList<Enemigo> enemigos = new ArrayList<>(Arrays.asList(enemigosArr));

        // pa iniciar
        Combate combate = new Combate(heroes, enemigos);
        combate.iniciar();
    }
}

