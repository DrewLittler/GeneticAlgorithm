package org.yorkshirecode;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class Frame extends JFrame implements ActionListener, ChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(Frame.class);

    private static JFileChooser fc = null;

    private List<JSlider> sliders = new ArrayList<>();
    private List<JSpinner> spinners = new ArrayList<>();
    private int suspendChangeListener = 0;

    private JSpinner children = null;
    private JSpinner generations = null;
    private JPanel topRight = null;
    private JPanel middleRight = null;
    private JTabbedPane tabbedPane = null;

    private State startingState = null;

    public Frame() {

        this.setTitle("Genetic Algorithm");
        this.setSize(new Dimension(1000, 1000));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(10);
        splitPane.setDividerLocation(0.5d);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        splitPane.add(left, JSplitPane.LEFT);

        tabbedPane = new JTabbedPane();
        splitPane.add(tabbedPane, JSplitPane.RIGHT);

        JPanel right = new JPanel();
        right.setLayout(new BorderLayout());
        tabbedPane.addTab("Preview", right);

        JPanel topLeft = new JPanel();
        left.add(topLeft, BorderLayout.NORTH);
        topLeft.setLayout(new FlowLayout(FlowLayout.LEADING));

        JButton b = new JButton("Random");
        topLeft.add(b);
        b.addActionListener(this);

        b = new JButton("Load");
        topLeft.add(b);
        b.addActionListener(this);

        topLeft.add(new JLabel("Children"));

        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(1);
        model.setMaximum(127);

        children = new Spinner();
        topLeft.add(children);
        children.setModel(model);
        children.setValue(4);
        children.addChangeListener(this);

        topLeft.add(new JLabel("Generations"));

        model = new SpinnerNumberModel();
        model.setMinimum(1);
        model.setMaximum(127);

        generations = new Spinner();
        topLeft.add(generations);
        generations.setModel(model);
        generations.setValue(4);
        generations.addChangeListener(this);

        b = new JButton("Go!");
        b.addActionListener(this);
        topLeft.add(b);

        GridLayout grid = new GridLayout();
        grid.setRows(State.GENE_COUNT);
        grid.setColumns(1);



        JPanel middleLeft = new JPanel();

        BoxLayout box = new BoxLayout(middleLeft, BoxLayout.Y_AXIS);
        middleLeft.setLayout(box);

        //middleLeft.setLayout(grid);
        left.add(middleLeft, BorderLayout.CENTER);

        Hashtable<Integer, JLabel> ticks = new Hashtable<>();
        for (int tick=State.MIN_VALUE; tick<=State.MAX_VALUE; tick++) {
            ticks.put(new Integer(tick), new JLabel("" + tick));
        }

        for (int i=0; i<State.GENE_COUNT; i++) {

            JPanel sliderPanel = new JPanel();
            sliderPanel.setLayout(new BorderLayout());
            middleLeft.add(sliderPanel);

            JLabel l = new JLabel();
            l.setText(State.getGeneDesc(i));
            sliderPanel.add(l, BorderLayout.WEST);

            JSlider slider = new Slider();
            slider.setMinimum(State.MIN_VALUE);
            slider.setMaximum(State.MAX_VALUE);
            slider.setMajorTickSpacing(1);
            slider.setMinorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintTrack(true);
            slider.setLabelTable(ticks);
            slider.addChangeListener(this);
            sliderPanel.add(slider, BorderLayout.CENTER);
            sliders.add(slider);

            model = new SpinnerNumberModel();
            model.setMinimum(State.MIN_VALUE);
            model.setMaximum(State.MAX_VALUE);

            JSpinner spinner = new Spinner();
            spinner.setModel(model);
            spinner.addChangeListener(this);
            sliderPanel.add(spinner, BorderLayout.EAST);
            spinners.add(spinner);


        }

        topRight = new JPanel();
        topRight.setLayout(new BorderLayout());
        topRight.setPreferredSize(new Dimension(0, 200));
        topRight.setBorder(BorderFactory.createLineBorder(Color.blue));
        right.add(topRight, BorderLayout.NORTH);

        WrapLayout wrapLayout = new WrapLayout();
        wrapLayout.setVgap(20);
        wrapLayout.setHgap(20);

        middleRight = new JPanel();
        middleRight.setLayout(wrapLayout);

        JScrollPane sp = new JScrollPane();
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setViewportView(middleRight);
        sp.getVerticalScrollBar().setUnitIncrement(100);

        right.add(sp, BorderLayout.CENTER);

        randomise();
     }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equalsIgnoreCase("Random")) {
            randomise();
        } else if (e.getActionCommand().equalsIgnoreCase("Go!")) {
            go();
        } else if (e.getActionCommand().equalsIgnoreCase("Load")) {
            load();
        } else {
            LOG.error("Unknown button [" + e.getActionCommand() + "]");
        }
    }

    private static JFileChooser getFileChooser() {
        if (fc == null) {
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        return fc;
    }

    private void load() {
        JFileChooser fc = getFileChooser();
        int ret = fc.showDialog(this, "Select");
        if (ret != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try{
            File f = fc.getSelectedFile();
            String name = FilenameUtils.removeExtension(f.getName());
            String[] toks = name.split(" ");
            if (toks.length != State.GENE_COUNT) {
                throw new IllegalArgumentException("Wrong number of tokens in file name");
            }

            int[] vals = new int[toks.length];

            for (int i=0; i<vals.length; i++) {
                vals[i] = Integer.parseInt(toks[i]);
            }

            for (int i=0; i<vals.length; i++) {
                JSlider slider = sliders.get(i);
                slider.setValue(vals[i]);
            }

        } catch (Exception e) {
            String err = "The file name isn't valid:\n" + e.getMessage();
            JOptionPane.showMessageDialog(this, err);
        }
    }

    private int getChildren() {
        return (Integer)children.getValue();
    }
    private int getGenerations() {
        return (Integer)generations.getValue();
    }

    private void go() {

        middleRight.removeAll();

        List<State> states = new ArrayList<>();
        states.add(startingState);
        go(middleRight, states, getChildren(), getGenerations());

        middleRight.revalidate();
        middleRight.validate();
        middleRight.invalidate();
    }
    private static void go(JPanel panel, List<State> states, int numChildren, int numGenerations) {

        numGenerations --;

        for (int i=0; i<numChildren; i++) {

            State lastState = states.get(states.size()-1);
            State nextState = lastState.mutate();

            final List<State> newStates = new ArrayList<>(states);
            newStates.add(nextState);

            if (numGenerations > 0) {
                go(panel, newStates, numChildren, numGenerations);
            } else {

                Page p = new Page(newStates);
                p.setPreferredSize(new Dimension(210, 300));
                panel.add(p);

                GraphicalTooltip tooltip = new GraphicalTooltip(p, p);
            }
        }

    }



    private void randomise() {

        suspendChangeListener ++;
        Random r = new Random();

        for (int i=0; i<sliders.size(); i++) {

            JSlider slider = sliders.get(i);
            int min = slider.getMinimum();
            int max = slider.getMaximum();
            int val = min + r.nextInt(max - min);
            slider.setValue(val);

            JSpinner spinner = spinners.get(i);
            spinner.setValue(new Integer(val));

        }
        suspendChangeListener --;
        createState();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (suspendChangeListener > 0) {
            return;
        }

        suspendChangeListener ++;

        if (e.getSource() instanceof JSpinner) {
            JSpinner spinner = (JSpinner)e.getSource();
            int val = ((Integer)spinner.getValue()).intValue();
            int index = spinners.indexOf(spinner);

            //the spinners on the top panel aren't linked to sliders
            if (index > -1) {
                JSlider slider = sliders.get(index);
                slider.setValue(val);
            }

        } else {
            JSlider slider = (JSlider)e.getSource();
            int val = slider.getValue();
            int index = sliders.indexOf(slider);
            JSpinner spinner = spinners.get(index);
            spinner.setValue(new Integer(val));
        }

        suspendChangeListener --;

        createState();
    }

    private void createState() {
        int[] genes = new int[State.GENE_COUNT];
        for (int i=0; i<sliders.size(); i++) {
            JSlider slider = sliders.get(i);
            genes[i] = slider.getValue();
        }

        this.startingState = new State(genes);

        StatePanel panel = new StatePanel(startingState);

        panel.setBorder(BorderFactory.createLineBorder(Color.RED));
        topRight.removeAll();
        topRight.add(panel, BorderLayout.CENTER);

        //topRight.repaint();
        topRight.validate();
        topRight.invalidate();
        topRight.revalidate();
    }

    public void evolve(List<State> states) {

        WrapLayout wrapLayout = new WrapLayout();
        wrapLayout.setVgap(20);
        wrapLayout.setHgap(20);

        JPanel p = new JPanel();
        p.setLayout(wrapLayout);

        JScrollPane sp = new JScrollPane();
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setViewportView(p);
        sp.getVerticalScrollBar().setUnitIncrement(100);

        int tab = tabbedPane.getSelectedIndex()+1;

        String tabName = states.get(0).getGeneString(".") + " (" + (states.size() + getGenerations()) + ")";
        tabbedPane.insertTab(tabName, null, sp, null, tab);

        //JLabel l = (JLabel)tabbedPane.getTabComponentAt(tab);
        //l.set

        go(p, states, getChildren(), getGenerations());

        tabbedPane.setSelectedIndex(tab);


    }
}
