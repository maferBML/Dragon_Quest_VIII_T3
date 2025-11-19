package vista;

import controlador.ControlJuego;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VentanaInicio extends JFrame {

    private Image imagenFondo;
    private final ControlJuego control;

    public VentanaInicio(ControlJuego control) {
        this.control = control;

        setTitle("Reino de Trodain - Dragon Quest RPG");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        imagenFondo = new ImageIcon(getClass().getResource("/foticos/bosque.jpg")).getImage();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 6));

        JLabel titulo = new JLabel("DRAGON QUEST VIII: El Despertar de Trodain", JLabel.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton btnIniciar = new JButton("⚔️ Iniciar Batalla");
        btnIniciar.setFont(new Font("Serif", Font.BOLD, 24));
        btnIniciar.setBackground(new Color(30, 144, 225));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);
        btnIniciar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

        btnIniciar.addActionListener((ActionEvent e) -> {
            dispose();
            new VentanaBatalla(control);
        });

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(btnIniciar, BorderLayout.CENTER);
        add(panel);
        setVisible(true);
    }
}
