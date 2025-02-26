package fr.ul.miage.ncmnf;

import javax.swing.*;
import java.awt.*;

public class PanelRnW extends JPanel {
    NFCReaderWorker readerWorker;
    private boolean isWorkerRunning = false;

    public PanelRnW(JFrame frame) {
        this.setLayout(new BorderLayout());
        this.setSize(1000, 800);
        JTextArea textArea = new JTextArea();
        textArea.setMaximumSize(new Dimension(500, 400));
        textArea.setEditable(false);
        this.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JTextField urlField = new JTextField(20);
        JButton writeButton = new JButton("Écrire sur la puce");
        JButton cancelButton = new JButton("switch mode");
        readerWorker = new NFCReaderWorker(textArea, frame);
        writeButton.addActionListener(e -> {
            String url = urlField.getText();
            if (!url.isEmpty()) {
                // Exécuter le worker d'écriture
                NFCWriterWorker writerWorker = new NFCWriterWorker(frame, url);
                writerWorker.execute();
                writerWorker.addPropertyChangeListener(NFCWriterWorker.DATA_CHANGED, readerWorker);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une URL.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("URL:"));
        inputPanel.add(urlField);
        inputPanel.add(writeButton);
        this.add(inputPanel, BorderLayout.SOUTH);
        this.add(cancelButton, BorderLayout.NORTH);

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
