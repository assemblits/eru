package inside;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.eru.scene.control.ArcClock;

import static com.eru.scene.control.ArcClock.BLUE1;
import static com.eru.scene.control.ArcClock.BLUE2;

//
//import java.text.ParseException;
//
//public class DynamoTest extends Application {
//
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    @Override
//    public void start(Stage stage) throws ParseException {
//        AnchorPane newAnchorPane = new AnchorPane();
//        stage.setScene(new Scene(newAnchorPane));
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}

public class DynamoTest extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {

        Group root = new Group();
        Scene scene = new Scene(root, 650, 220, Color.TRANSPARENT);

        // create a canvas node
        Canvas canvas = new Canvas();

        // bind the dimensions when the user resizes the window.
        canvas.widthProperty().bind(primaryStage.widthProperty());
        canvas.heightProperty().bind(primaryStage.heightProperty());

        // obtain the GraphicsContext (drawing surface)
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        // create three clocks
        final ArcClock blueClock = new ArcClock(20, BLUE1, BLUE2, 200);
        blueClock.update(0);
//         create an animation (update & render loop)
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // update clocks
                blueClock.update(now);

                // clear screen
                gc.clearRect(0, 0, primaryStage.getWidth(), primaryStage.getHeight());

                // draw blue clock
                blueClock.draw(gc);
                // save the origin or the current state
                // of the Graphics Context.
//                gc.save();

//                // shift x coord position the width of a clock plus 20 pixels
//                gc.translate(blueClock.maxDiameter + 20, 0);
//                greenClock.draw(gc);
//
//                // shift x coord position past the first clock
//                gc.translate(blueClock.maxDiameter + 20, 0);
//                redClock.draw(gc);

                // reset Graphics Context to last saved point.
                // Translate x, y to (0,0)
//                gc.restore();

            }
        }.start();

        // add the single node onto the scene graph
        root.getChildren().add(canvas);
        root.setStyle("-fx-background-color: transparent");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}