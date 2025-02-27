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
            mainPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            frame.add(mainPanel);

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
            frame.add(switchBtn, BorderLayout.SOUTH);
            frame.setVisible(true);

        });
    }

}