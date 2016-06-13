package org.yorkshirecode;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Page extends JPanel implements TooltipProviderI, MouseListener, ActionListener {

    private static JFileChooser fc = null;

    private List<State> states = null;

    public Page(List<State> states) {
        this.states = states;

        GridLayout l = new GridLayout();
        l.setColumns(1);
        l.setRows(states.size());
        l.setVgap(5);

        setOpaque(true);
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout(l);

        for (State s: states) {
            add(new StatePanel(s));
        }

        addMouseListener(this);
    }

    private int getStateHeight(int h) {
        int stateCount = states.size();
        h /= (stateCount * 6);
        return h * 5;
        /*h /= (stateCount * 4);
        return h * 3;*/
    }
    private int getVerticalStep(int h) {
        int stateCount = states.size();
        h /= stateCount;
        return h;
    }

    public List<State> getStates() {
        return states;
    }

    @Override
    public Dimension getTooltipSize() {
        return new Dimension(getWidth()*2, getHeight()*2);
    }

    @Override
    public void paintTooltip(Graphics g, int w, int h) {

        g = g.create();

        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);

        int stateHeight = getStateHeight(h);
        int verticalStep = getVerticalStep(h);

        for (State s: states) {
            s.paint(g, w, stateHeight);
            g.translate(0, verticalStep);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            showPopup(e.getPoint());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void showPopup(Point p) {
        JPopupMenu m = new JPopupMenu();

        JMenuItem i = new JMenuItem("Save");
        i.addActionListener(this);
        m.add(i);

        i = new JMenuItem("Evolve");
        i.addActionListener(this);
        m.add(i);

        i = new JMenuItem("Remove");
        i.addActionListener(this);
        m.add(i);

        m.show(this, p.x, p.y);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Save")) {
            save();
        } else if (e.getActionCommand().equals("Evolve")) {
            evolve();
        } else if (e.getActionCommand().equals("Remove")) {
            remove();
        } else {

        }
    }

    private void remove() {
        Container cont = getParent();
        cont.remove(this);

        cont.validate();
        cont.revalidate();
        cont.invalidate();
    }

    private void evolve() {

        Frame frame = findFrame(this);
        frame.evolve(states);
    }
    private Frame findFrame(Component component) {
        if (component instanceof Frame) {
            return (Frame)component;
        }

        return findFrame(component.getParent());
    }

    private static JFileChooser getFileChooser() {
        if (fc == null) {
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        return fc;
    }

    private void save() {

        JFileChooser fc = getFileChooser();
        int ret = fc.showDialog(this, "Select");
        if (ret != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try{
            File parentDir = fc.getSelectedFile();

            State firstState = states.get(0);
            String name = firstState.getGeneString();

            File f = new File(parentDir, name + ".svg");
            if (f.exists()) {
                int attempt = 1;
                f = new File(parentDir, name + " " + attempt + ".svg");
                while (f.exists()) {
                    attempt ++;
                    f = new File(parentDir, name + " " + attempt + ".svg");
                }
            }

            writeToFile(f);

        } catch (Exception e) {
            String err = "Error saving:\n" + e.getMessage();
            JOptionPane.showMessageDialog(this, err);
        }

    }

    private void writeToFile(File f) throws Exception {

        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        int w = getWidth();
        int h = getHeight();

        svgGenerator.setSVGCanvasSize(new Dimension(w, h));

        int stateHeight = getStateHeight(h);
        int verticalStep = getVerticalStep(h);

        int y = 0;
        for (State state: states) {
            state.paintToSvg(svgGenerator, 0, y, w, stateHeight);

            y+= verticalStep;
        }

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes

        FileWriter out = new FileWriter(f);

        //Writer out = new OutputStreamWriter(System.out, "UTF-8");
        svgGenerator.stream(out, useCSS);

        out.close();
    }
}
