package fr.ul.miage.ncmnf;

import javax.swing.*;
import java.awt.*;

public class PanelBadge extends JPanel {

    public PanelBadge() {
        this.setLayout(new BorderLayout());
        this.setSize(1000, 800);
        this.add(new JLabel("Mode Badge ON"), BorderLayout.CENTER);
    }

    public void switchMode(boolean visible) {
        this.setVisible(visible);
    }
}
