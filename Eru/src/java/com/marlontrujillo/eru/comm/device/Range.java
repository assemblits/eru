package com.marlontrujillo.eru.comm.device;


import com.marlontrujillo.eru.util.Preferences;

/**
 *
 * @author marlon
 */
public class Range {
    /* ********** Static Fields ********** */
    public static final int MAXIMUN_TOTAL = Preferences.getInstance().getModbusBlockMaxLimit();

    /* ********** Fields ********** */
    private int         start;
    private int         end;
    private int         total;

    /* ********** Constructor ********** */
    public Range(int start, int end) {
        this.start      = start;
        this.end        = end;
        this.total      = end - (start - 1);
    }

    /* ********** Setters and Getters ********** */
    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "[" + start + ", (" + total + "), " + end + "]";
    }
}
