package org.yorkshirecode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphicalTooltip implements MouseMotionListener, MouseListener, HierarchyListener, ActionListener {

    private Component component = null;
    private Timer timer = null;
    private Point mouseLoc = null;
    private TooltipProviderI provider = null;
    private TooltipPanel tooltipPanel = null;

    public GraphicalTooltip(Component component, TooltipProviderI provider) {
        this.component = component;
        this.provider = provider;

        this.timer = new Timer(500, this);
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

        mouseLoc = e.getPoint();
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
        mouseLoc = e.getPoint();
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
    }

    private void hideTooltip() {
        if (tooltipPanel == null) {
            return;
        }

        tooltipPanel.setVisible(false);
        Container parent = tooltipPanel.getParent();
        parent.remove(tooltipPanel);
        tooltipPanel = null;
    }

    private void showTooltip() {

        Window win = getWindow(component);
        if (win == null) {
            return;
        }

        tooltipPanel = new TooltipPanel(provider);

        if (win instanceof JFrame) {
            ((JFrame)win).getLayeredPane().add(tooltipPanel, JLayeredPane.POPUP_LAYER);
        } else if (win instanceof JDialog) {
            ((JDialog)win).getLayeredPane().add(tooltipPanel, JLayeredPane.POPUP_LAYER);
        } else {
            throw new RuntimeException("Unsupported window type");
        }

        Point loc = SwingUtilities.convertPoint(component, mouseLoc, win);
        tooltipPanel.setLocation(loc);
    }

    private Window getWindow(Component comp) {
        Container cont = comp.getParent();
        if (cont instanceof Window) {
            return (Window)cont;
        }

        return getWindow(cont);
    }
}
