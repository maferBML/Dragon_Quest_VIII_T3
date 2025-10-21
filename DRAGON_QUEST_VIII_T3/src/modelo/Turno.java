package modelo;
public class Turno {

    private Personaje personajeA;

    public Turno(Personaje personajeA) {
        this.personajeA = personajeA;
    }

    public void ejecutarTurno(Personaje personajeB) {
        if (personajeA.estaVivo()){
            personajeA.atacar(personajeB);
        } else {
            System.out.println(personajeA.getNombre() + " está incapacitado y no puede actuar.");
        }
    
    }
}
