package fr.ul.miage.ncmnf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        Object[] possibleValues = { "Lecture/Écriture de Puce", "Badgeuse"};

        Object selectedValue = JOptionPane.showInputDialog(null,
                "Quel mode de l'application voulez-vous exécuter ?", "Choix mode",
                JOptionPane.INFORMATION_MESSAGE, null,
                possibleValues, possibleValues[0]);


        Color background = new Color(220,244,177);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Swamp Near Field Communications");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 800);

            JPanel mainPanel = new JPanel();
            mainPanel.setVisible(true);
            frame.add(mainPanel);
            mainPanel.setBackground(background);

            if(selectedValue == "Lecture/Écriture de Puce") {
                PanelRnW panelRnW = new PanelRnW(frame);
                panelRnW.setVisible(true);
                mainPanel.add(panelRnW);
            }
            else if(selectedValue == "Badgeuse") {
                PanelBadge panelBadge = new PanelBadge();
                panelBadge.setVisible(true);
                mainPanel.add(panelBadge);
            }
            else {
                System.exit(0);
            }


            frame.setVisible(true);

        });
    }

}