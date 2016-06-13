package org.yorkshirecode;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;

public class StatePanel extends JPanel {

    private State state = null;

    public StatePanel(State state) {
        this.state = state;
    }

    @Override
    public void paintComponent(Graphics g2) {

        super.paintComponent(g2);
        state.paint(g2, getWidth(), getHeight());
    }
}
