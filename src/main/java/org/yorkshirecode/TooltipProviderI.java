package org.yorkshirecode;

import java.awt.*;

public interface TooltipProviderI {

    public Dimension getTooltipSize();
    public void paintTooltip(Graphics g, int w, int h);
}
