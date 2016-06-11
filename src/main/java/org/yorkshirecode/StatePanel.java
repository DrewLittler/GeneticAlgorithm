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

        int cols = state.get(Gene.COLUMNS) + 5;
        double colWidth = (double)getWidth()/ (double)cols;

        int r = 25 + (state.get(Gene.START_RED) * 20);
        int g = 25 + (state.get(Gene.START_GREEN) * 20);
        int b = 25 + (state.get(Gene.START_BLUE) * 20);
        Color col = null;
        try {
            col = new Color(r, g, b);
        } catch (IllegalArgumentException e) {
            System.out.println("R " + r + " G " + g + " B " + b);
            e.printStackTrace();
        }
        //Color col = new Color(r, g, b);

        int half = (State.MAX_VALUE - State.MIN_VALUE) / 2;
        double deltaH = (double)(state.get(Gene.DELTA_HUE) - half) / 100d;
        double deltaS = (double)(state.get(Gene.DELTA_SATURATION) - half) / 100d;
        double deltaB = (double)(state.get(Gene.DELTA_BRIGHTNESS) - half) / 100d;

        int x = 0;
        for (int i=0; i<cols; i++) {

            g2.setColor(col);
            g2.fillRect(x, 0, (int)colWidth, getHeight());

            float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);

            if (hsb[0] + deltaH > 1d) {
                deltaH = -deltaH;
            }
            hsb[0] += deltaH;

            if (hsb[1] + deltaS > 1d) {
                deltaS = -deltaS;
            }
            hsb[1] += deltaS;

            if (hsb[2] + deltaB > 1d) {
                deltaB = -deltaB;
            }
            hsb[2] += deltaB;

            col = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
            x += colWidth;
        }

    }
}
