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

        Color background = new Color(220,244,177);
        Color buttons = new Color(122,146,68);
        Color buttonText = new Color(243,242,225);

        SwingUtilities.invokeLater(() -> {
            AtomicBoolean badgeMode = new AtomicBoolean(false);
            JFrame frame = new JFrame("Swamp Near Field Communications");
            Font Arial = new Font("Arial", Font.BOLD, 16);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 800);

            JPanel mainPanel = new JPanel();
            mainPanel.setVisible(true);
            frame.add(mainPanel);
            mainPanel.setBackground(background);

            PanelRnW panelRnW = new PanelRnW(frame);
            panelRnW.setVisible(true);
            mainPanel.add(panelRnW);

            PanelBadge panelBadge = new PanelBadge();
            panelBadge.setVisible(false);
            mainPanel.add(panelBadge);

            JButton switchBtn = new JButton("Changer de mode");
            switchBtn.setFont(Arial);
            switchBtn.setPreferredSize(new Dimension(300, 40));
            switchBtn.setBackground(buttons);
            switchBtn.setForeground(buttonText);
            switchBtn.addActionListener(e -> {
                boolean newMode = !badgeMode.get();
                badgeMode.set(newMode);
                panelBadge.switchMode(newMode);
                panelRnW.switchMode(!newMode);
            });
            mainPanel.add(switchBtn);
            frame.setVisible(true);

        });
    }

}