package vista;

import controlador.ControlJuego;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Vista de la batalla, integrada con ControlJuego.
 * - No ejecuta lógica del modelo: pide acciones al controlador.
 * - Muestra el log y el estado (HP/MP).
 */
public class VentanaBatalla extends JFrame {

    private final ControlJuego control;
    private Image fondo;
    private JTextArea cuadroTexto;
    private JPanel panelHeroes, panelEnemigos, panelAcciones;
    private List<Heroe> heroes;
    private List<Enemigo> enemigos;
    private ArrayList<JLabel> labelsHeroes = new ArrayList<>();
    private ArrayList<JPanel> panelsEnemigos = new ArrayList<>();

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

        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelFondo.setLayout(new BorderLayout());
        panelFondo.setBorder(BorderFactory.createLineBorder(Color.WHITE, 6));

        // Panel Héroes (arriba)
        panelHeroes = new JPanel(new GridLayout(1, heroes.size(), 10, 10));
        panelHeroes.setOpaque(false);
        panelHeroes.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        for (Heroe h : heroes) agregarHeroe(h);

        // Panel Enemigos (centro)
        panelEnemigos = new JPanel(new GridLayout(1, enemigos.size(), 15, 15));
        panelEnemigos.setOpaque(false);
        panelEnemigos.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        for (Enemigo e : enemigos) agregarEnemigo(e);

        // Cuadro de texto y acciones (inferior)
        cuadroTexto = new JTextArea(8, 20);
        cuadroTexto.setEditable(false);
        cuadroTexto.setWrapStyleWord(true);
        cuadroTexto.setLineWrap(true);
        cuadroTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        cuadroTexto.setBackground(new Color(20, 20, 50));
        cuadroTexto.setForeground(Color.WHITE);
        cuadroTexto.setText(control.getLog());

        panelAcciones = new JPanel();
        panelAcciones.setBackground(new Color(10, 10, 30));
        panelAcciones.setOpaque(false);

        JButton btnAtacar = crearBoton("Atacar (elige enemigo)");
        JButton btnHabilidad = crearBoton("Habilidad");
        JButton btnResetLog = crearBoton("Limpiar Log");

        // Atacar: instruye al usuario a que elija un enemigo (los botones de enemigos realizan la acción)
        btnAtacar.addActionListener(e -> cuadroTexto.setText("Elige un enemigo (botón debajo de cada enemigo)"));

        // Habilidad: abre diálogo para seleccionar habilidad del héroe actual y objetivo
        btnHabilidad.addActionListener(e -> abrirDialogoHabilidad());

        btnResetLog.addActionListener(e -> {
            control.limpiarLog();
            cuadroTexto.setText(control.getLog());
        });

        panelAcciones.add(btnAtacar);
        panelAcciones.add(btnHabilidad);
        panelAcciones.add(btnResetLog);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        panelInferior.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        panelInferior.add(new JScrollPane(cuadroTexto), BorderLayout.CENTER);
        panelInferior.add(panelAcciones, BorderLayout.SOUTH);

        panelFondo.add(panelHeroes, BorderLayout.NORTH);
        panelFondo.add(panelEnemigos, BorderLayout.CENTER);
        panelFondo.add(panelInferior, BorderLayout.SOUTH);

        add(panelFondo);
        setVisible(true);
    }

    private void agregarHeroe(Heroe h) {
        JLabel lbl = new JLabel(
            "<html><center><b>" + h.getNombre() + "</b><br>HP: " + h.getVidaHp() + "<br>MP: " + h.getMagiaMp() + "</center></html>",
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
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel("<html><center>" + e.getNombre() + "</center></html>", JLabel.CENTER);
        lbl.setForeground(e.esMiniJefe() ? Color.ORANGE : Color.RED);
        lbl.setFont(new Font("Serif", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createLineBorder(e.esMiniJefe() ? Color.ORANGE : Color.RED, 2));
        lbl.setOpaque(false);
        p.add(lbl, BorderLayout.CENTER);

        JButton btn = crearBoton("Atacar");
        btn.addActionListener(ev -> {
            // ejecutar ataque del héroe actual contra este enemigo
            String nuevoLog = control.atacarEnemigo(e);
            cuadroTexto.setText(nuevoLog);
            actualizarLabelsHeroes();
            actualizarPanelEnemigos(); // actualizar si enemigo murió
        });
        p.add(btn, BorderLayout.SOUTH);

        panelsEnemigos.add(p);
        panelEnemigos.add(p);
    }

    private void actualizarLabelsHeroes() {
        for (int i = 0; i < heroes.size(); i++) {
            Heroe h = heroes.get(i);
            labelsHeroes.get(i).setText(
                "<html><center><b>" + h.getNombre() +
                "</b><br>HP: " + h.getVidaHp() +
                "<br>MP: " + h.getMagiaMp() + "</center></html>"
            );
        }
    }

    private void actualizarPanelEnemigos() {
        // Re-dibuja nombres/colores si murieron
        for (int i = 0; i < enemigos.size(); i++) {
            Enemigo e = enemigos.get(i);
            JPanel p = panelsEnemigos.get(i);
            // el label está en la posición BorderLayout.CENTER
            Component c = p.getComponent(0);
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel)c;
                if (!e.estaVivo()) {
                    lbl.setText("<html><center>💀 " + e.getNombre() + "</center></html>");
                    lbl.setForeground(Color.GRAY);
                    // deshabilitar botón
                    if (p.getComponentCount() > 1 && p.getComponent(1) instanceof JButton) {
                        JButton b = (JButton)p.getComponent(1);
                        b.setEnabled(false);
                    }
                }
            }
        }
    }

    private JButton crearBoton(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Serif", Font.BOLD, 16));
        b.setBackground(new Color(30, 144, 255));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return b;
    }

    // Diálogo para elegir habilidad del héroe actual y objetivo
    private void abrirDialogoHabilidad() {
        Heroe actual = obtenerHeroeActualDesdeControl();
        if (actual == null) {
            JOptionPane.showMessageDialog(this, "No hay héroes vivos para usar habilidades.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Habilidad> habs = actual.getHabilidades();
        if (habs.isEmpty()) {
            JOptionPane.showMessageDialog(this, actual.getNombre() + " no tiene habilidades.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Crear opciones de lista de habilidades
        String[] opciones = new String[habs.size()];
        for (int i = 0; i < habs.size(); i++) {
            Habilidad h = habs.get(i);
            opciones[i] = (i+1) + ". " + h.getNombre() + " (" + h.getTipo() + ", MP:" + h.getCosteMp() + ")";
        }

        String seleccion = (String) JOptionPane.showInputDialog(
            this,
            "Elige una habilidad:",
            "Habilidades",
            JOptionPane.PLAIN_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (seleccion == null) return; // cancel

        int indice = Integer.parseInt(seleccion.split("\\.")[0]) - 1;
        Habilidad hElegida = habs.get(indice);

        if (hElegida.getTipo().equalsIgnoreCase("curación") || hElegida.getTipo().equalsIgnoreCase("curacion")) {
            // elegir aliado a curar
            String[] aliados = heroes.stream().filter(Heroe::estaVivo).map(Heroe::getNombre).toArray(String[]::new);
            String elegido = (String) JOptionPane.showInputDialog(this, "Elige aliado a curar:", "Curación", JOptionPane.PLAIN_MESSAGE, null, aliados, aliados[0]);
            if (elegido == null) return;
            Heroe aliado = heroes.stream().filter(h -> h.getNombre().equals(elegido)).findFirst().orElse(null);
            if (aliado != null) {
                String nuevoLog = control.usarHabilidadCuracion(indice, aliado);
                cuadroTexto.setText(nuevoLog);
                actualizarLabelsHeroes();
                actualizarPanelEnemigos();
            }
        } else {
            // elegir enemigo objetivo
            List<Enemigo> vivos = new ArrayList<>();
            for (Enemigo e : enemigos) if (e.estaVivo()) vivos.add(e);
            if (vivos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay enemigos vivos.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] opcionesEn = vivos.stream().map(Enemigo::getNombre).toArray(String[]::new);
            String elegido = (String) JOptionPane.showInputDialog(this, "Elige enemigo objetivo:", "Habilidad", JOptionPane.PLAIN_MESSAGE, null, opcionesEn, opcionesEn[0]);
            if (elegido == null) return;
            Enemigo objetivo = vivos.stream().filter(en -> en.getNombre().equals(elegido)).findFirst().orElse(null);
            if (objetivo != null) {
                String nuevoLog = control.usarHabilidadEnemigo(indice, objetivo);
                cuadroTexto.setText(nuevoLog);
                actualizarLabelsHeroes();
                actualizarPanelEnemigos();
            }
        }
    }

    // Helper: obtener héroe actual consultando al controlador (coincide con su lógica)
    private Heroe obtenerHeroeActualDesdeControl() {
        // ControlJuego no expone directamente el índice, así que deducimos por estado:
        for (Heroe h : control.getHeroes()) {
            if (h.estaVivo()) {
                return h; // la vista no necesita exactitud sobre cuál es "actual" para elegir habilidades; control usa su propia rotación
            }
        }
        return null;
    }
}
