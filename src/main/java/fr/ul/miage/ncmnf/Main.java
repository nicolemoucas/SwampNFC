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
            frame.setSize(600, 600);

            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

            JTextField urlField = new JTextField(20);
            JButton writeButton = getWriteButton(urlField, frame);

            JPanel inputPanel = new JPanel();
            inputPanel.add(new JLabel("URL:"));
            inputPanel.add(urlField);
            inputPanel.add(writeButton);
            frame.add(inputPanel, BorderLayout.SOUTH);

            frame.setVisible(true);

            // Exécuter le worker de lecture
            NFCReaderWorker readerWorker = new NFCReaderWorker(textArea, frame);
            readerWorker.execute();
        });
    }

    private static JButton getWriteButton(JTextField urlField, JFrame frame) {
        JButton writeButton = new JButton("Écrire sur la puce");

        writeButton.addActionListener(e -> {
            String url = urlField.getText();
            if (!url.isEmpty()) {
                // Exécuter le worker d'écriture
                NFCWriterWorker writerWorker = new NFCWriterWorker(frame, url);
                writerWorker.execute();
            } else {
                JOptionPane.showMessageDialog(frame, "Veuillez entrer une URL.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        return writeButton;
    }
}