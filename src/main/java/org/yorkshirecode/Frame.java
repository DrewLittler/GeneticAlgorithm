package org.yorkshirecode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class Frame extends JFrame implements ActionListener, ChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(Frame.class);

    private List<JSlider> sliders = new ArrayList<>();
    private int suspendChangeListener = 0;

    private JSpinner children = null;
    private JSpinner generations = null;
    private JPanel topRight = null;
    private JPanel middleRight = null;

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

        JPanel right = new JPanel();
        right.setLayout(new BorderLayout());
        splitPane.add(right, JSplitPane.RIGHT);

        JPanel topLeft = new JPanel();
        left.add(topLeft, BorderLayout.NORTH);
        topLeft.setLayout(new FlowLayout(FlowLayout.LEADING));

        JButton b = new JButton("Random");
        topLeft.add(b);
        b.addActionListener(this);

        topLeft.add(new JLabel("Children"));

        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum(1);
        model.setMaximum(127);

        children = new JSpinner();
        topLeft.add(children);
        children.setModel(model);
        children.setValue(4);
        children.addChangeListener(this);

        topLeft.add(new JLabel("Generations"));

        model = new SpinnerNumberModel();
        model.setMinimum(1);
        model.setMaximum(127);

        generations = new JSpinner();
        topLeft.add(generations);
        generations.setModel(model);
        generations.setValue(14);
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

            JSlider slider = new JSlider();
            slider.setMinimum(State.MIN_VALUE);
            slider.setMaximum(State.MAX_VALUE);
            slider.setMajorTickSpacing(1);
            slider.setMinorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintTrack(true);
            slider.setLabelTable(ticks);
            sliderPanel.add(slider, BorderLayout.CENTER);

            slider.addChangeListener(this);

            sliders.add(slider);
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
        } else {
            LOG.error("Unknown button [" + e.getActionCommand() + "]");
        }
    }

    private void go() {

        middleRight.removeAll();

        int numChildren = (Integer)children.getValue();
        int numGenerations = (Integer)generations.getValue();

        List<State> states = new ArrayList<>();
        states.add(startingState);
        go(states, numChildren, numGenerations);

        middleRight.revalidate();
        middleRight.validate();
        middleRight.invalidate();
    }
    private void go(List<State> states, int numChildren, int numGenerations) {

        numGenerations --;

        for (int i=0; i<numChildren; i++) {

            State lastState = states.get(states.size()-1);
            State nextState = lastState.mutate();

            List<State> newStates = new ArrayList<>(states);
            newStates.add(nextState);

            if (numGenerations > 0) {
                go(newStates, numChildren, numGenerations);
            } else {

                GridLayout l = new GridLayout();
                l.setColumns(1);
                l.setRows(newStates.size());
                l.setVgap(5);

                JPanel p = new JPanel();
                p.setOpaque(true);
                p.setBackground(Color.white);
                p.setBorder(BorderFactory.createLineBorder(Color.black));
                p.setLayout(l);

                for (State s: newStates) {
                    p.add(new StatePanel(s));
                }

                p.setPreferredSize(new Dimension(210, 300));
                middleRight.add(p);

                GraphicalTooltip tooltip = new GraphicalTooltip(p);
            }
        }

    }



    private void randomise() {

        suspendChangeListener ++;
        Random r = new Random();

        for (JSlider slider: sliders) {
            int min = slider.getMinimum();
            int max = slider.getMaximum();
            int val = min + r.nextInt(max - min);
            slider.setValue(val);
        }
        suspendChangeListener --;
        createState();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (suspendChangeListener > 0) {
            return;
        }

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
}
