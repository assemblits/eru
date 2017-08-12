package com.eru.scene.control;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by mtrujillo on 5/17/2016.
 */
public class ArcClock {
    public static Color BLUE1 = Color.rgb(126, 166, 212, 0.6);
    public static Color BLUE2 = Color.rgb(126, 166, 222, 0.5);
    public static Color BLUE3 = Color.rgb(130, 166, 230, 0.5);
    public static Color GREEN1 = Color.rgb(130, 230, 166, 0.5);
    public static Color RED1 = Color.rgb(230, 130, 166, 0.5);

    public ArcPiece longPiece;
    public ArcPiece[] arcPieces;
    public int maxDiameter;
    public double radius;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddhhmmssa");

    public ArcClock(int numArcs, Color longPieceColor, Color manyPieceColor, int maxDiameter) {
        this.maxDiameter = maxDiameter;
        radius = maxDiameter / 2;
        longPiece = ArcPieceBuilder.create()
                .strokeColor(longPieceColor)
                .strokeWidth(5)
                .x(0)
                .y(0)
                .w(maxDiameter)
                .h(maxDiameter)
                .startAngle(45)
                .arcExtent(240)
                .displayTimePerFrameMillis(1000)
                .pixelsToMove(1)
                .build();

        arcPieces = createRandomArcs(numArcs, manyPieceColor, maxDiameter / 2);
    }

    public void update(long now){
        longPiece.update(now);
        for (ArcPiece ap:arcPieces) {
            ap.update(now);
        }
    }

    public void draw(GraphicsContext gc) {
        longPiece.draw(gc);
        for (ArcPiece ap:arcPieces) {
            ap.draw(gc);
        }
        // draw hour
        gc.setFont(Font.font("Calibri", 40));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);

        String dateTimeStr = DATE_FORMAT.format(new Date());
        //yyyyMMddhhmmssa
        gc.fillText(dateTimeStr.substring(8, 10) , radius, radius + 18 );
        gc.setFont(Font.font("Calibri", 20));
        gc.fillText(dateTimeStr.substring(10, 12) + " " + dateTimeStr.substring(14) , maxDiameter - 40, radius - 40 );
        gc.fillText(dateTimeStr.substring(12, 14) , maxDiameter - 40, maxDiameter - 40 );

    }

    public static int randomIntRange(int min, int max) {
        Random rand = new Random();
        int range = max - min + 1;
        return rand.nextInt(range) + min;
    }

    public static ArcPiece[] createRandomArcs(int num, Color color, double radius) {
        final ArcPiece[] manyPieces = new ArcPiece[num];
        for (int i=0; i<num; i++) {
            manyPieces[i] = randomArcPiece(color, radius);
        }
        return manyPieces;
    }
    public static ArcPiece randomArcPiece(Color color, double radius) {

        int width =  randomIntRange(60, (int) radius * 2);
        int randomStrokeWidth = randomIntRange(1,10);
        int randomStartAngle = randomIntRange(1, 270);
        int randomExtentAngle = randomIntRange(10, 360-randomStartAngle);
        long randomMillis = randomIntRange(0, 33);
        Color someColor = color;
        if (color == null) {
            someColor =  BLUE1;
        }
        final ArcPiece arcPiece = ArcPieceBuilder.create()
                .strokeColor(someColor)
                .strokeWidth(randomStrokeWidth)
                .x(radius - (width/2))
                .y(radius - (width/2))
                .w(width)
                .h(width)
                .startAngle(randomStartAngle)
                .arcExtent(randomExtentAngle)
                .displayTimePerFrameMillis(randomMillis)
                .pixelsToMove(2)
                .build();
        arcPiece.clockwise = new Random().nextBoolean();

        return arcPiece;
    }
}
class ArcPiece {
    public double x;
    public double y;
    public double w;
    public double h;
    public double startAngle;
    public double arcExtent;
    public double strokeWidth = 2;
    public double pixelsToMove = 2;
    public Color strokeColor;
    public boolean clockwise=false;

    long startTime = 0;
    public long displayTimePerFrameMillis = 60;
    private long displayTimePerFrameNano = 60  * 1000000;

    public void update(long now) {
        if (startTime == 0){
            startTime = now;
            displayTimePerFrameNano = displayTimePerFrameMillis * 1000000;
        }

        long elapsed = now - startTime;
        if (elapsed > displayTimePerFrameNano) {
            if (!clockwise){
                startAngle = startAngle + pixelsToMove;
                if (startAngle > 360){
                    startAngle = 0;
                }
            } else {
                startAngle = startAngle - pixelsToMove;
                if (startAngle < -360){
                    startAngle = 0;
                }
            }
            startTime = 0;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);
        gc.strokeArc(x,
                y,
                w,
                h,
                startAngle,
                arcExtent,
                ArcType.OPEN);
    }
}
class ArcPieceBuilder {
    ArcPiece arcPiece = new ArcPiece();
    public static ArcPieceBuilder create() {
        return new ArcPieceBuilder();
    }
    public ArcPieceBuilder x(double x){
        arcPiece.x = x;
        return this;
    }
    public ArcPieceBuilder y(double y){
        arcPiece.y = y;
        return this;
    }
    public ArcPieceBuilder w(double w){
        arcPiece.w = w;
        return this;
    }
    public ArcPieceBuilder h(double h){
        arcPiece.h = h;
        return this;
    }
    public ArcPieceBuilder startAngle(double startAngle){
        arcPiece.startAngle = startAngle;
        return this;
    }
    public ArcPieceBuilder arcExtent(double arcExtent){
        arcPiece.arcExtent = arcExtent;
        return this;
    }

    public ArcPieceBuilder strokeWidth(double width){
        arcPiece.strokeWidth = width;
        return this;
    }
    public ArcPieceBuilder strokeColor(Color c){
        arcPiece.strokeColor = c;
        return this;
    }

    public ArcPieceBuilder clockwise(){
        arcPiece.clockwise = true;
        return this;
    }
    public ArcPieceBuilder counterClockwise(){
        arcPiece.clockwise = false;
        return this;
    }

    public ArcPieceBuilder displayTimePerFrameMillis(long millis){
        arcPiece.displayTimePerFrameMillis = millis;
        return this;
    }

    public ArcPieceBuilder pixelsToMove(double numPixels){
        arcPiece.pixelsToMove = numPixels;
        return this;
    }

    public ArcPiece build() {
        return arcPiece;
    }
}