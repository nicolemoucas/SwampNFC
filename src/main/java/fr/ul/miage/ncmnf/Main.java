package fr.ul.miage.ncmnf;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        SwingUtilities.invokeLater(() -> {
            AtomicBoolean badgeMode = new AtomicBoolean(false);
            JFrame frame = new JFrame("Swamp Near Field Communications");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 800);


            JPanel mainPanel = new JPanel();
            mainPanel.setVisible(true);
            frame.add(mainPanel, BorderLayout.CENTER);


            PanelRnW panelRnW = new PanelRnW(frame);
            panelRnW.setVisible(true);
            mainPanel.add(panelRnW);

            PanelBadge panelBadge = new PanelBadge();
            panelBadge.setVisible(false);
            mainPanel.add(panelBadge);


            JButton switchBtn = new JButton("switch");
            switchBtn.addActionListener(e -> {
                boolean newMode = !badgeMode.get();
                badgeMode.set(newMode);
                panelBadge.switchMode(newMode);
                panelRnW.switchMode(!newMode);
            });
            frame.add(switchBtn, BorderLayout.NORTH);
            frame.setVisible(true);



//            frame.setLayout(new BorderLayout());
//            frame.setSize(1000, 800);
//            JTextArea textArea = new JTextArea();
//            textArea.setMaximumSize(new Dimension(500, 400));
//            textArea.setEditable(false);
//            //frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
//
//            JTextField urlField = new JTextField(20);
//            JButton writeButton = new JButton("Écrire sur la puce");
//            JButton cancelButton = new JButton("switch mode");
//            NFCReaderWorker readerWorker = new NFCReaderWorker(textArea, frame);
//
//            writeButton.addActionListener(e -> {
//                String url = urlField.getText();
//                if (!url.isEmpty()) {
//                    // Exécuter le worker d'écriture
//                    NFCWriterWorker writerWorker = new NFCWriterWorker(frame, url);
//                    writerWorker.execute();
//                    writerWorker.addPropertyChangeListener(NFCWriterWorker.DATA_CHANGED, readerWorker);
//                } else {
//                    JOptionPane.showMessageDialog(frame, "Veuillez entrer une URL.", "Erreur", JOptionPane.ERROR_MESSAGE);
//                }
//            });
//
//            JPanel inputPanel = new JPanel();
//            inputPanel.add(new JLabel("URL:"));
//            inputPanel.add(urlField);
//            inputPanel.add(writeButton);
//            frame.add(inputPanel, BorderLayout.SOUTH);
//            frame.add(cancelButton, BorderLayout.NORTH);
//
//            JPanel p2 = new JPanel();
//            p2.setLayout(new BorderLayout());
//            JTextArea textArea2 = new JTextArea();
//            textArea2.setMaximumSize(new Dimension(500, 400));
//            readerWorker.execute();



        });
    }

}