package fr.ul.miage.ncmnf;

import javax.swing.*;
import java.awt.*;

public class PanelRnW extends JPanel {
    SNFCReaderWorker readerWorker;
    private boolean isWorkerRunning;

    public PanelRnW(JFrame frame) {
        Color background = new Color(220,244,177);
        Color buttons = new Color(122,146,68);
        Color textColor = new Color(92,69,45);
        Color buttonText = new Color(243,242,225);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS ));
        this.setPreferredSize(new Dimension(900, 700));
        this.setBackground(background);

        Font ArialMTBold = new Font("Arial Rounded MT Bold", Font.BOLD, 45);
        Font Titre2 = new Font("Arial Rounded MT Bold", Font.ITALIC, 20);
        Font Arial = new Font("Arial", Font.BOLD, 16);

        JLabel titre = new JLabel();
        titre.setFont(ArialMTBold);
        titre.setText("Swamp Near Field Communications");
        titre.setForeground(textColor);
        titre.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(leftJustify(titre));

        JLabel sousTitre = new JLabel();
        sousTitre.setFont(Titre2);
        sousTitre.setText("Contenu de la puce");
        sousTitre.setForeground(textColor);
        sousTitre.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(leftJustify(sousTitre));

        JTextArea textArea = new JTextArea();
        textArea.setFont(Arial);
        textArea.setEditable(false);
        textArea.setForeground(textColor);
        textArea.setBackground(background);
        textArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(textColor), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        this.add(new JScrollPane(textArea));

        JLabel sousTitre2 = new JLabel();
        sousTitre2.setFont(Titre2);
        sousTitre2.setText("Écriture sur la puce");
        sousTitre2.setForeground(textColor);
        sousTitre2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(leftJustify(sousTitre2));

        JTextField textField = new JTextField(56);
        textField.setFont(Arial);
        textField.setForeground(textColor);
        textField.setBackground(background);
        textField.setBorder(BorderFactory.createLineBorder(textColor));
        JButton writeButton = new JButton("Écraser");
        writeButton.setForeground(buttonText);
        writeButton.setBackground(buttons);
        writeButton.setFont(Arial);
        readerWorker = new SNFCReaderWorker(textArea, frame);
        writeButton.addActionListener(e -> {
            String text = textField.getText();
            if (!text.isEmpty()) {
                // Exécuter le worker d'écriture
                SNFCWriterWorker writerWorker = new SNFCWriterWorker(frame, text);
                writerWorker.execute();
                writerWorker.addPropertyChangeListener(SNFCWriterWorker.DATA_CHANGED, readerWorker);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une valeur.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(background);
        inputPanel.add(textField);
        inputPanel.add(writeButton);
        this.add(inputPanel);

        readerWorker.execute();
        isWorkerRunning = true;
    }

    private Component leftJustify( JLabel label )  {
        Box  b = Box.createHorizontalBox();
        b.add( label );
        b.add( Box.createHorizontalGlue() );
        return b;
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
