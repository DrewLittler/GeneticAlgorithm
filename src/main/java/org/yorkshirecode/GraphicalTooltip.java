package org.yorkshirecode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphicalTooltip implements MouseMotionListener, MouseListener, HierarchyListener, ActionListener {

    private Component component = null;
    private Timer timer = null;
    private TooltipProviderI provider = null;

    public GraphicalTooltip(Component component, TooltipProviderI provider) {
        this.component = component;
        this.provider = provider;

        this.timer = new Timer(750, this);
        //timer.addActionListener(this);
        timer.setRepeats(false);
        //timer.setDelay(1000);

        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addHierarchyListener(this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        hideTooltip();
        timer.restart();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {
        timer.start();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hideTooltip();
        timer.stop();
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        if (component.getParent() == null) {
            component.removeMouseListener(this);
            component.removeMouseMotionListener(this);
            component.removeHierarchyListener(this);

            timer.removeActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showTooltip();
        System.out.println("Show tooltip");
    }

    private void hideTooltip() {

    }

    private void showTooltip() {

    }
}
