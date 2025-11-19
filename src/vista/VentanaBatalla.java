package vista;

import controlador.ControlJuego;
import modelo.Enemigo;
import modelo.Heroe;
import modelo.Personaje;
import modelo.Habilidad;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VentanaBatalla extends JFrame {

    private enum ModoAccion { NINGUNO, ATACAR, HABILIDAD_ENEMIGO }

    private ControlJuego control;

    private Image fondo;
    private JTextArea cuadroTexto;
    private JPanel panelHeroes, panelEnemigos, panelAcciones;

    private ArrayList<Heroe> heroes;
    private ArrayList<Enemigo> enemigos;
    private ArrayList<JLabel> labelsHeroes = new ArrayList<>();
    private ArrayList<JPanel> panelesEnemigos = new ArrayList<>();
    private ArrayList<JButton> botonesEnemigos = new ArrayList<>();

    private int indiceHeroeActual = 0;
    private Random random = new Random();
    private ModoAccion modoActual = ModoAccion.NINGUNO;

    private JButton btnAtacar;
    private JButton btnDefender;
    private JButton btnHabilidad;
    private JButton btnSalir;

    private Habilidad habilidadSeleccionada = null; // para habilidades que van a enemigo

    public VentanaBatalla(ControlJuego control) {
        this.control = control;
        this.heroes = control.getHeroes();
        this.enemigos = control.getEnemigos();

        setTitle("⚔️ Batalla en el Reino de Trodain");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        fondo = new ImageIcon(getClass().getResource("/foticos/bosque.jpg")).getImage();

        construirInterfaz();

        Heroe actual = obtenerHeroeActual();
        if (actual != null) {
            cuadroTexto.append("\nTurno inicial de: " + actual.getNombre() + "\n");
        }

        setVisible(true);
    }

    // ===================== INTERFAZ =====================

    private void construirInterfaz() {
        JPanel panelFondo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) {
                    g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panelFondo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Héroes
        panelHeroes = new JPanel(new GridLayout(1, heroes.size(), 15, 15));
        panelHeroes.setOpaque(false);
        panelHeroes.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        for (Heroe h : heroes) {
            agregarHeroe(h);
        }

        // Enemigos
        panelEnemigos = new JPanel(new GridLayout(1, enemigos.size(), 15, 15));
        panelEnemigos.setOpaque(false);
        panelEnemigos.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        for (Enemigo e : enemigos) {
            agregarEnemigo(e);
        }

        // Cuadro de texto
        cuadroTexto = new JTextArea(8, 20);
        cuadroTexto.setEditable(false);
        cuadroTexto.setWrapStyleWord(true);
        cuadroTexto.setLineWrap(true);
        cuadroTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        cuadroTexto.setBackground(new Color(20, 20, 50));
        cuadroTexto.setForeground(Color.WHITE);

        cuadroTexto.setText(mensajeJefeInicial());
        cuadroTexto.append("\n💥 ¡Comienza la batalla! 💥\n");

        // Panel de acciones
        panelAcciones = new JPanel();
        panelAcciones.setBackground(new Color(10, 10, 30));

        btnAtacar = crearBoton("Atacar");
        btnDefender = crearBoton("Defender");
        btnHabilidad = crearBoton("Habilidad");
        btnSalir = crearBoton("Salir");

        btnAtacar.addActionListener(e -> {
            Heroe h = obtenerHeroeActual();
            if (h == null) {
                cuadroTexto.append("\nNo quedan héroes vivos.\n");
                return;
            }
            modoActual = ModoAccion.ATACAR;
            habilidadSeleccionada = null;
            cuadroTexto.append("\n" + h.getNombre() + " se prepara para atacar. Elige un enemigo.\n");
        });

        btnDefender.addActionListener(e -> {
            Heroe h = obtenerHeroeActual();
            if (h == null) {
                cuadroTexto.append("\nNo quedan héroes vivos.\n");
                return;
            }
            cuadroTexto.append("\n👉 Turno de: " + h.getNombre() + "\n");
            cuadroTexto.append(h.defenderTexto());
            finTurnoJugador();
            turnoEnemigo();
        });

        btnHabilidad.addActionListener(e -> manejarHabilidad());

        btnSalir.addActionListener(e -> System.exit(0));

        panelAcciones.add(btnAtacar);
        panelAcciones.add(btnDefender);
        panelAcciones.add(btnHabilidad);
        panelAcciones.add(btnSalir);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(10, 10, 30));
        panelInferior.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        panelInferior.add(new JScrollPane(cuadroTexto), BorderLayout.CENTER);
        panelInferior.add(panelAcciones, BorderLayout.SOUTH);

        panelFondo.add(panelHeroes, BorderLayout.NORTH);
        panelFondo.add(panelEnemigos, BorderLayout.CENTER);
        panelFondo.add(panelInferior, BorderLayout.SOUTH);

        add(panelFondo);
    }

    // ===================== COMPONENTES AUXILIARES =====================

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Serif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(40, 40, 90));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return btn;
    }

    private void agregarHeroe(Heroe h) {
        JLabel lbl = new JLabel(
                "<html><center><b>" + h.getNombre() + "</b><br>HP: "
                        + h.getVidaHp() + "<br>MP: " + h.getMagiaMp() + "</center></html>",
                JLabel.CENTER
        );
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Serif", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        lbl.setOpaque(false);

        panelHeroes.add(lbl);
        labelsHeroes.add(lbl);
    }

    private void agregarEnemigo(Enemigo e) {
        JPanel panelE = new JPanel(new BorderLayout());
        panelE.setOpaque(false);

        JLabel lbl = new JLabel("<html><center>" + e.getNombre() + "</center></html>", JLabel.CENTER);
        lbl.setForeground(e.esMiniJefe() ? Color.ORANGE : Color.RED);
        lbl.setFont(new Font("Serif", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createLineBorder(e.esMiniJefe() ? Color.ORANGE : Color.RED, 2));
        lbl.setOpaque(false);
        panelE.add(lbl, BorderLayout.CENTER);

        JButton btn = crearBoton("Objetivo");

        btn.addActionListener(ev -> enemigoClicado(e, panelE, btn));

        panelE.add(btn, BorderLayout.SOUTH);

        panelEnemigos.add(panelE);
        panelesEnemigos.add(panelE);
        botonesEnemigos.add(btn);
    }

    private String mensajeJefeInicial() {
        StringBuilder sb = new StringBuilder();
        for (Enemigo e : enemigos) {
            if (e.esMiniJefe()) {
                sb.append("¡Un ").append(e.getNombre().toUpperCase())
                        .append(" ha aparecido como JEFE (Cagaste)!\n")
                        .append("HP aumentado: ").append(e.getVidaHp()).append("\n")
                        .append("Ataque aumentado: ").append(e.getAtaque()).append("\n")
                        .append("Defensa aumentada: ").append(e.getDefensa()).append("\n");
                break;
            }
        }
        sb.append("==============================================\n");
        return sb.toString();
    }

    // ===================== LÓGICA DE TURNO =====================

    private Heroe obtenerHeroeActual() {
        int intentos = 0;
        while (intentos < heroes.size()) {
            Heroe h = heroes.get(indiceHeroeActual);
            if (h.estaVivo()) {
                return h;
            }
            indiceHeroeActual = (indiceHeroeActual + 1) % heroes.size();
            intentos++;
        }
        return null;
    }

    private Enemigo elegirEnemigoVivoAleatorio() {
        ArrayList<Enemigo> vivos = new ArrayList<>();
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) vivos.add(e);
        }
        if (vivos.isEmpty()) return null;
        return vivos.get(random.nextInt(vivos.size()));
    }

    private Heroe elegirHeroeVivoAleatorio() {
        ArrayList<Heroe> vivos = new ArrayList<>();
        for (Heroe h : heroes) {
            if (h.estaVivo()) vivos.add(h);
        }
        if (vivos.isEmpty()) return null;
        return vivos.get(random.nextInt(vivos.size()));
    }

    private boolean hayVivos(List<? extends Personaje> lista) {
        for (Personaje p : lista) {
            if (p.estaVivo()) return true;
        }
        return false;
    }

    private void actualizarHeroes() {
        for (int i = 0; i < heroes.size(); i++) {
            Heroe h = heroes.get(i);
            JLabel lbl = labelsHeroes.get(i);
            if (!h.estaVivo()) {
                lbl.setVisible(false); // desaparece de la pantalla
            } else {
                lbl.setText("<html><center><b>" + h.getNombre() + "</b><br>HP: "
                        + h.getVidaHp() + "<br>MP: " + h.getMagiaMp() + "</center></html>");
                lbl.setForeground(Color.WHITE);
            }
        }
        panelHeroes.revalidate();
        panelHeroes.repaint();
    }

    private void deshabilitarTodo() {
        for (JButton b : botonesEnemigos) {
            b.setEnabled(false);
        }
        btnAtacar.setEnabled(false);
        btnDefender.setEnabled(false);
        btnHabilidad.setEnabled(false);
    }

    private void enemigoClicado(Enemigo enemigo, JPanel panelEnemigo, JButton botonEnemigo) {
        Heroe atacante = obtenerHeroeActual();
        if (atacante == null) {
            cuadroTexto.append("\nNo quedan héroes vivos.\n");
            return;
        }

        if (modoActual == ModoAccion.ATACAR) {
            ejecutarAtaqueBasico(atacante, enemigo, panelEnemigo, botonEnemigo);
            modoActual = ModoAccion.NINGUNO;
            habilidadSeleccionada = null;
        } else if (modoActual == ModoAccion.HABILIDAD_ENEMIGO && habilidadSeleccionada != null) {
            ejecutarHabilidadEnemigo(atacante, habilidadSeleccionada, enemigo, panelEnemigo, botonEnemigo);
            modoActual = ModoAccion.NINGUNO;
            habilidadSeleccionada = null;
        } else {
            cuadroTexto.append("\nPrimero elige 'Atacar' o 'Habilidad'.\n");
        }
    }

    private void ejecutarAtaqueBasico(Heroe atacante, Enemigo objetivo, JPanel panelEnemigo, JButton botonEnemigo) {
        if (objetivo == null || !objetivo.estaVivo()) {
            cuadroTexto.append("Ese enemigo ya está derrotado.\n");
            return;
        }

        cuadroTexto.append("\n👉 Turno de: " + atacante.getNombre() + "\n");
        cuadroTexto.append(atacante.atacarTexto(objetivo));

        if (!objetivo.estaVivo()) {
            cuadroTexto.append("💥 " + objetivo.getNombre() + " ha sido derrotado.\n");
            panelEnemigo.setVisible(false);   // desaparece
            botonEnemigo.setEnabled(false);
        }

        if (!hayVivos(enemigos)) {
            cuadroTexto.append("\n🏆 ¡HAS GANADO LA BATALLA!\n");
            deshabilitarTodo();
            actualizarHeroes();
            cuadroTexto.setCaretPosition(cuadroTexto.getText().length());
            return;
        }

        finTurnoJugador();
        turnoEnemigo();
    }

    // === HABILIDADES ===
    private void manejarHabilidad() {
        Heroe h = obtenerHeroeActual();
        if (h == null) {
            cuadroTexto.append("\nNo quedan héroes vivos.\n");
            return;
        }

        ArrayList<Habilidad> habilidades = h.getHabilidades();
        if (habilidades.isEmpty()) {
            cuadroTexto.append("\n" + h.getNombre() + " no tiene habilidades.\n");
            return;
        }

        String[] nombres = new String[habilidades.size()];
        for (int i = 0; i < habilidades.size(); i++) {
            Habilidad hab = habilidades.get(i);
            nombres[i] = (i + 1) + ". " + hab.getNombre() + " (MP: " + hab.getCosteMp() + ")";
        }

        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Elige una habilidad:",
                "Habilidades de " + h.getNombre(),
                JOptionPane.PLAIN_MESSAGE,
                null,
                nombres,
                nombres[0]
        );

        if (seleccion == null) {
            return; // canceló
        }

        int indice = 0;
        for (int i = 0; i < nombres.length; i++) {
            if (nombres[i].equals(seleccion)) {
                indice = i;
                break;
            }
        }

        Habilidad hSel = habilidades.get(indice);
        String tipo = hSel.getTipo().toLowerCase();

        if (tipo.equals("curación")) {
            // Elegir aliado
            ArrayList<Heroe> vivos = new ArrayList<>();
            for (Heroe her : heroes) if (her.estaVivo()) vivos.add(her);

            if (vivos.isEmpty()) {
                cuadroTexto.append("\nNo hay aliados vivos para curar.\n");
                return;
            }

            String[] nombresAliados = new String[vivos.size()];
            for (int i = 0; i < vivos.size(); i++) {
                Heroe ha = vivos.get(i);
                nombresAliados[i] = ha.getNombre() + " (HP: " + ha.getVidaHp() + ")";
            }

            String selAliado = (String) JOptionPane.showInputDialog(
                    this,
                    "¿A quién quieres curar?",
                    "Elegir aliado",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    nombresAliados,
                    nombresAliados[0]
            );

            if (selAliado == null) return;

            Heroe objetivoHeroe = vivos.get(0);  // <- CORRECTO

    for (int i = 0; i < nombresAliados.length; i++) {
        if (nombresAliados[i].equals(selAliado)) {
            objetivoHeroe = vivos.get(i); // <- TAMBIÉN CORRECTO
            break;
        }
    }


            cuadroTexto.append("\n👉 Turno de: " + h.getNombre() + "\n");
            cuadroTexto.append(
                    h.usarHabilidadGUI(hSel, heroes, objetivoHeroe, null)
            );

            actualizarHeroes();
            finTurnoJugador();
            turnoEnemigo();

        } else {
            // daño / estado: se elige enemigo haciendo click
            habilidadSeleccionada = hSel;
            modoActual = ModoAccion.HABILIDAD_ENEMIGO;
            cuadroTexto.append("\n" + h.getNombre() + " prepara " + hSel.getNombre()
                    + ". Elige un enemigo como objetivo.\n");
        }
    }

    private void ejecutarHabilidadEnemigo(Heroe atacante,
                                          Habilidad hab,
                                          Enemigo objetivo,
                                          JPanel panelEnemigo,
                                          JButton botonEnemigo) {

        if (objetivo == null || !objetivo.estaVivo()) {
            cuadroTexto.append("Ese enemigo ya está derrotado.\n");
            return;
        }

        cuadroTexto.append("\n👉 Turno de: " + atacante.getNombre() + "\n");
        cuadroTexto.append(
                atacante.usarHabilidadGUI(hab, heroes, null, objetivo)
        );

        if (!objetivo.estaVivo()) {
            cuadroTexto.append("💥 " + objetivo.getNombre() + " ha sido derrotado.\n");
            panelEnemigo.setVisible(false);
            botonEnemigo.setEnabled(false);
        }

        if (!hayVivos(enemigos)) {
            cuadroTexto.append("\n🏆 ¡HAS GANADO LA BATALLA!\n");
            deshabilitarTodo();
            actualizarHeroes();
            cuadroTexto.setCaretPosition(cuadroTexto.getText().length());
            return;
        }

        actualizarHeroes();
        finTurnoJugador();
        turnoEnemigo();
    }

    private void turnoEnemigo() {
        if (!hayVivos(enemigos) || !hayVivos(heroes)) return;

        Enemigo enemigoAtaca = elegirEnemigoVivoAleatorio();
        Heroe heroeObjetivo = elegirHeroeVivoAleatorio();

        if (enemigoAtaca == null || heroeObjetivo == null) return;

        cuadroTexto.append("\n⚠️ Turno del enemigo: " + enemigoAtaca.getNombre() + "\n");
        cuadroTexto.append(
                enemigoAtaca.accionAutomaticaTexto(heroeObjetivo)
        );

        if (!heroeObjetivo.estaVivo()) {
            cuadroTexto.append("💀 " + heroeObjetivo.getNombre() + " ha sido derrotado.\n");
        }

        if (!hayVivos(heroes)) {
            cuadroTexto.append("\n💀 ¡TU EQUIPO HA SIDO DERROTADO!\n");
            deshabilitarTodo();
        }

        actualizarHeroes();
        cuadroTexto.setCaretPosition(cuadroTexto.getText().length());
    }

    private void finTurnoJugador() {
        actualizarHeroes();
        indiceHeroeActual = (indiceHeroeActual + 1) % heroes.size();
        cuadroTexto.setCaretPosition(cuadroTexto.getText().length());
    }
}
