package com.eru.gui.trend;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by mtrujillo on 5/13/2016.
 */
public enum TrendColor {

    BURLYWOOD       ("BURLYWOOD",      "BURLYWOOD, white"),
    CORAL           ("CORAL",          "CORAL, white"),
    GOLDENROD       ("GOLDENROD",      "GOLDENROD, white"),
    ROSYBROWN       ("ROSYBROWN",      "ROSYBROWN, white"),
    POWDERBLUE      ("POWDERBLUE",     "POWDERBLUE, white"),
    SLATEGRAY       ("SLATEGRAY",      "SLATEGRAY, white"),
    STEELBLUE       ("STEELBLUE",      "STEELBLUE, white"),
    SIENNA          ("SIENNA",         "SIENNA, white"),
    INDIANRED       ("INDIANRED",      "INDIANRED, white"),
    DARKCYAN        ("DARKCYAN",       "DARKCYAN, white"),
    LIGHTSTEELBLUE  ("LIGHTSTEELBLUE", "LIGHTSTEELBLUE, white"),
    DARKBLUE        ("CORNFLOWERBLUE", "CORNFLOWERBLUE, white"),
    DARKGREEN       ("DARKGREEN",      "DARKGREEN, white"),
    DARKOLIVEGREEN  ("DARKOLIVEGREEN", "DARKOLIVEGREEN, white"),
    DARKSALMON      ("DARKSALMON",     "DARKSALMON, white");

    private static final List<TrendColor> VALUES     = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int              SIZE       = VALUES.size();
    private static final Random           RANDOM     = new Random();
    public static final TrendColor        DEFAULT    = SLATEGRAY;
    private String                        background = "";
    private String                        stroke     = "";

    TrendColor(String background, String stroke) {
        this.background = background;
        this.stroke = stroke;
    }

    public static TrendColor randomColor()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    public String getCssRule(){
        return "-fx-background-color: "+background+"; "+"-fx-stroke: "+stroke+";";
    }

    public String getBackground() {
        return background;
    }
    public String getStroke() {
        return stroke;
    }
}
