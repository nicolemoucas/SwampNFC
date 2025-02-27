package fr.ul.miage.ncmnf;

import javax.swing.*;
import java.awt.*;

public class PanelRnW extends JPanel {
    SNFCReaderWorker readerWorker;
    private boolean isWorkerRunning;

    public PanelRnW(JFrame frame) {
        this.setLayout(new BorderLayout());
        this.setSize(1000, 800);
        this.setBorder(BorderFactory.createLineBorder(Color.RED));
        JTextArea textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(500, 400));
        textArea.setEditable(false);
        this.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JTextField textField = new JTextField(20);
        JButton writeButton = new JButton("Écrire sur la puce");
        readerWorker = new SNFCReaderWorker(textArea, frame);
        writeButton.addActionListener(e -> {
            String text = textField.getText();
            if (!text.isEmpty()) {
                // Exécuter le worker d'écriture
                SNFCWriterWorker writerWorker = new SNFCWriterWorker(frame, text);
                writerWorker.execute();
                writerWorker.addPropertyChangeListener(SNFCWriterWorker.DATA_CHANGED, readerWorker);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une URL.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("URL:"));
        inputPanel.add(textField);
        inputPanel.add(writeButton);
        this.add(inputPanel, BorderLayout.SOUTH);

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        JTextArea textArea2 = new JTextArea();
        textArea2.setMaximumSize(new Dimension(500, 400));
        readerWorker.execute();
        isWorkerRunning = true;
    }

    public void switchMode(boolean visible) {
        this.setVisible(visible);
        if (visible) {
            if(!isWorkerRunning) {
                readerWorker.execute();
                isWorkerRunning = true;
            }
        } else {
            readerWorker.cancel(true);
            isWorkerRunning = false;
        }
    }
}
