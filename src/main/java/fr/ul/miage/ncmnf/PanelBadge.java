package fr.ul.miage.ncmnf;

import javax.swing.*;
import java.awt.*;

public class PanelBadge extends JPanel {
    SNFCTimeClockWorker timeClockWorker;
    private boolean isWorkerRunning;

    public PanelBadge() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setSize(1000, 800);
        this.setMinimumSize(new Dimension(1000,800));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JTextArea textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(500, 400));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setMinimumSize(new Dimension(500, 400));
        this.add(scrollPane);


        timeClockWorker = new SNFCTimeClockWorker();
        timeClockWorker.addPropertyChangeListener(SNFCTimeClockWorker.DATA_CHANGED, (e) -> {
            textArea.append(e.getNewValue() + "\n");
            this.repaint();
        });

        timeClockWorker.addPropertyChangeListener(SNFCTimeClockWorker.UNAUTHORIZED, (e) ->
                JOptionPane.showMessageDialog(this, "Entrée refusée, vous n'avez pas les droits " + e.getNewValue() , "Erreur", JOptionPane.ERROR_MESSAGE));

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
