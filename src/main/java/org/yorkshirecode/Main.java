package org.yorkshirecode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);



    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            LOG.error("Error setting L&F", ex);
        }

        //UIManager.getDefaults().put("TabbedPaneTab.contentMargins", new Insets(10, 100, 0, 0));
        //UIManager.getLookAndFeelDefaults().put("TabbedPane:TabbedPaneTab.contentMargins", new Insets(10, 100, 0, 0));

        Frame f = new Frame();
        f.setVisible(true);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);

        //TODO - handle variable columns proper;=ly

    }
}
