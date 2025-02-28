package fr.ul.miage.ncmnf;

import javax.swing.*;
import java.awt.*;

public class PanelBadge extends JPanel {
    SNFCTimeClockWorker timeClockWorker;
    private boolean isWorkerRunning;

    public PanelBadge() {
        Color background = new Color(220,244,177);
        Color textColor = new Color(92,69,45);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS ));
        this.setSize(1000, 800);
        this.setMinimumSize(new Dimension(1000,800));
        this.setBackground(background);

        Font ArialMTBold = new Font("Arial Rounded MT Bold", Font.BOLD, 45);
        Font Arial = new Font("Arial", Font.BOLD, 16);

        JLabel titre = new JLabel();
        titre.setFont(ArialMTBold);
        titre.setText("Swamp Near Field Communications");
        titre.setForeground(textColor);
        titre.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        this.add(leftJustify(titre));


        JTextArea textArea = new JTextArea();
        textArea.setBackground(background);
        textArea.setFont(Arial);
        textArea.setForeground(textColor);
        textArea.setPreferredSize(new Dimension(500, 600));
        textArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(textColor), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane);


        timeClockWorker = new SNFCTimeClockWorker();
        timeClockWorker.addPropertyChangeListener(SNFCTimeClockWorker.DATA_CHANGED, (e) -> {
            textArea.append(e.getNewValue() + "\n");
            this.repaint();
        });

        timeClockWorker.addPropertyChangeListener(SNFCTimeClockWorker.UNAUTHORIZED, (e) ->
                JOptionPane.showMessageDialog(this, "Entrée refusée, vous n'avez pas les droits " + e.getNewValue() , "Erreur", JOptionPane.ERROR_MESSAGE));

        timeClockWorker.addPropertyChangeListener(SNFCTimeClockWorker.GO_TO_WORK, (e) ->
                JOptionPane.showMessageDialog(this, "Ca fait beaucoup là non ? Tu n'as pas du travail " + e.getNewValue() + "???", "Retourne travailler", JOptionPane.WARNING_MESSAGE));

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
                timeClockWorker.execute();
                isWorkerRunning = true;
            }
        } else {
            timeClockWorker.cancel(true);
            isWorkerRunning = false;
        }
    }
}
