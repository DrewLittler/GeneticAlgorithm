package org.yorkshirecode;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class TooltipPanel extends JPanel {

    private TooltipProviderI provider = null;

    public TooltipPanel(TooltipProviderI provider) {
        this.provider = provider;

        Border line = BorderFactory.createLineBorder(SystemColor.controlDkShadow);
        Border pad = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        setBorder(BorderFactory.createCompoundBorder(line, pad));
        setOpaque(true);
        setBackground(SystemColor.info);

        Dimension dim = provider.getTooltipSize();
        Insets ins = getBorder().getBorderInsets(this);
        int w = dim.width + ins.left + ins.right;
        int h = dim.height + ins.right + ins.left;
        setSize(w, h);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Insets ins = getBorder().getBorderInsets(this);
        g = g.create();
        g.translate(ins.left, ins.top);

        int w = getWidth() - (ins.left + ins.right);
        int h = getHeight() - (ins.top + ins.bottom);

        provider.paintTooltip(g, w, h);
    }


}
