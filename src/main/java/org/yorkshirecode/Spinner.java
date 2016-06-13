package org.yorkshirecode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Spinner extends JSpinner implements MouseWheelListener, FocusListener {

    public Spinner() {
        super();
        addMouseWheelListener(this);
    }

    @Override
    protected JComponent createEditor(SpinnerModel model) {
        DefaultEditor r = (DefaultEditor)super.createEditor(model);
        r.getTextField().addFocusListener(this);
        return r;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        Object val = null;
        if (e.getWheelRotation() < 0) {
            val = getModel().getNextValue();
        } else {
            val = getModel().getPreviousValue();
        }

        if (val != null) {
            setValue(val);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (!e.isTemporary()) {
            JTextField t = (JTextField)e.getSource();
            //t.selectAll();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    t.selectAll();
                }
            });
        }
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
