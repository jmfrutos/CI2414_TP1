package buscador;

import javax.imageio.IIOException;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Jose on 11/06/2016.
 */
public class Buscador extends JFrame {

    private JPanel panel1;
    private JPanel buscador_view;
    private JTextField textField1_consulta;
    private JButton buscarButton;
    private JTextPane textPane1_resultados;

    public Buscador() {
        textPane1_resultados.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (URISyntaxException | IOException e1) {
                        }
                        ;

                    }
                }
            }
        });
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Buscando...");

                if (textField1_consulta.getText().isEmpty()) {

                    JOptionPane.showMessageDialog(new Frame(), "No ha ingresado términos para buscar.", "¡Atención!", JOptionPane.WARNING_MESSAGE);
                } else {
                    Consulta c = new Consulta(textField1_consulta.getText());
                    textPane1_resultados.setText(c.buscar());
                }
            }
        });
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Buscador");
        frame.setContentPane(new Buscador().buscador_view);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
