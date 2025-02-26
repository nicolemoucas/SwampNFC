package fr.ul.miage.ncmnf;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Swamp Near Field Communications");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

            JTextField urlField = new JTextField(20);
            JButton writeButton = new JButton("Écrire sur la puce");
            NFCReaderWorker readerWorker = new NFCReaderWorker(textArea, frame);

            writeButton.addActionListener(e -> {
                String url = urlField.getText();
                if (!url.isEmpty()) {
                    // Exécuter le worker d'écriture
                    NFCWriterWorker writerWorker = new NFCWriterWorker(frame, url);
                    writerWorker.execute();
                    writerWorker.addPropertyChangeListener(NFCWriterWorker.DATA_CHANGED, readerWorker);
                } else {
                    JOptionPane.showMessageDialog(frame, "Veuillez entrer une URL.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });

            JPanel inputPanel = new JPanel();
            inputPanel.add(new JLabel("URL:"));
            inputPanel.add(urlField);
            inputPanel.add(writeButton);
            frame.add(inputPanel, BorderLayout.SOUTH);

            frame.setVisible(true);
            readerWorker.execute();

        });
    }

    public static void switchModes(boolean mode, JFrame frame1, JFrame frame2) {
        frame1.setVisible(!mode);
        frame2.setVisible(mode);
    }

}