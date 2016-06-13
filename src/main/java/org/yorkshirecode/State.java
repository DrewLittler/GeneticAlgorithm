package org.yorkshirecode;

import org.apache.batik.svggen.SVGGraphics2D;

import java.awt.*;
import java.util.Random;

public class State {

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 9;
    public static final int GENE_COUNT = 8;

    private static Random r = new Random();

    private int[] genes = null;

    public State() {
        this.genes = new int[GENE_COUNT];
    }
    public State(int [] genes) {
        this.genes = genes;
    }

    public int get(Gene gene) {
        return genes[gene.getValue()];
    }

    public String getGeneStringDesc() {
        return getGeneString(". ");
    }

    public String getGeneString() {
        return getGeneString(" ");
    }

    public String getGeneString(String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i: genes) {
            sb.append(i);
            sb.append(delim);
        }
        return sb.toString().trim();
    }

    public State mutate() {

        int mutations = get(Gene.MUTABILITY);

        int[] copy = new int[GENE_COUNT];
        System.arraycopy(genes, 0, copy, 0, GENE_COUNT);

        for (int i = 0; i < mutations; i++) {

            while (true) {

                int gene = r.nextInt(GENE_COUNT);
                int newVal = genes[gene];
                if (r.nextBoolean()) {
                    newVal++;
                } else {
                    newVal--;
                }

                //if out of bounds, ignore and try again
                if (newVal < MIN_VALUE
                        || newVal > MAX_VALUE) {
                    continue;
                }

                copy[gene] = newVal;
                break;
            }
        }

        return new State(copy);
    }

    public static String getGeneDesc(int i) {
        switch (i) {
            case 0:
                return "Start R";
            case 1:
                return "Start G";
            case 2:
                return "Start B";
            case 3:
                return "Columns";
            case 4:
                return "Delta Hue";
            case 5:
                return "Delta Sat";
            case 6:
                return "Delta Brgt";
            case 7:
                return "Mutability";
            default:
                return null;
        }
    }

    private Color getStartColour() {
        int r = 25 + (get(Gene.START_RED) * 20);
        int g = 25 + (get(Gene.START_GREEN) * 20);
        int b = 25 + (get(Gene.START_BLUE) * 20);
        Color col = null;
        try {
            col = new Color(r, g, b);
        } catch (IllegalArgumentException e) {
            System.out.println("R " + r + " G " + g + " B " + b);
            e.printStackTrace();
        }
        return col;
    }

    private Color getNextColour(Color col) {
        int half = (State.MAX_VALUE - State.MIN_VALUE) / 2;
        double deltaH = (double)(get(Gene.DELTA_HUE) - half) / 100d;
        double deltaS = (double)(get(Gene.DELTA_SATURATION) - half) / 100d;
        double deltaB = (double)(get(Gene.DELTA_BRIGHTNESS) - half) / 100d;

        float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);

        if (hsb[0] + deltaH > 1d) {
            deltaH = -deltaH;
        }
        hsb[0] += deltaH;

        if (hsb[1] + deltaS > 1d) {
            deltaS = -deltaS;
        }
        hsb[1] += deltaS;

        if (hsb[2] + deltaB > 1d) {
            deltaB = -deltaB;
        }
        hsb[2] += deltaB;

        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public void paintToSvg(SVGGraphics2D svgGenerator, int x, int y, int w, int h) {

        svgGenerator.setFont(new Font("Courier New", Font.PLAIN, 2));
        FontMetrics fm = svgGenerator.getFontMetrics();
        svgGenerator.setColor(Color.black);
        svgGenerator.drawString("  " + getGeneStringDesc(), x, y + h + fm.getAscent() + 1);

        int cols = get(Gene.COLUMNS) + 5;
        double colWidth = (double)w/ (double)cols;

        Color col = getStartColour();

        for (int i=0; i<cols; i++) {

            //if the last col, make sure to use up all the width
            if (i+1 == cols) {
                colWidth = w-x;
            }

            svgGenerator.setColor(col);
            svgGenerator.fillRect(x, y, (int)colWidth, h);

            col = getNextColour(col);
            x += colWidth;
        }

    }

    public void paint(Graphics g2, int w, int h) {

        int cols = get(Gene.COLUMNS) + 5;
        double colWidth = (double)w/ (double)cols;

        Color col = getStartColour();

        int x = 0;
        for (int i=0; i<cols; i++) {

            g2.setColor(col);

            //if the last col, make sure to use up all the width
            if (i+1 == cols) {
                colWidth = w-x;
            }

            g2.fillRect(x, 0, (int)colWidth, h);

            col = getNextColour(col);
            x += colWidth;
        }

    }
}
