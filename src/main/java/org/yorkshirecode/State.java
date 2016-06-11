package org.yorkshirecode;

import java.util.Random;

public class State {

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 9;
    public static final int GENE_COUNT = 7;

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

    public State mutate() {
        int gene = r.nextInt(GENE_COUNT);
        boolean up = r.nextBoolean();

        int newVal = genes[gene];
        if (up) {
            newVal ++;
        } else {
            newVal --;
        }

        if (newVal < MIN_VALUE
                || newVal > MAX_VALUE) {
            return mutate();
        }

        int[] copy = new int[GENE_COUNT];
        System.arraycopy(genes, 0, copy, 0, GENE_COUNT);
        copy[gene] = newVal;

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
            default:
                return null;
        }
    }
}
