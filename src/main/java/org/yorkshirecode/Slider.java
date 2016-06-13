package org.yorkshirecode;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Slider extends JSlider implements MouseWheelListener {

    public Slider() {
        addMouseWheelListener(this);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        int val = getValue();
        if (e.getWheelRotation() < 0) {
            val++;
            if (val > getModel().getMaximum()) {
                return;
            }
        } else {
            val--;
            if (val < getModel().getMinimum()) {
                return;
            }
        }

        setValue(val);
    }
}
