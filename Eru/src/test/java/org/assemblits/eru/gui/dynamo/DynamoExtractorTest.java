package org.assemblits.eru.gui.dynamo;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import org.assemblits.eru.gui.dynamo.base.Dynamo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by marlontrujillo1080 on 10/14/17.
 */
public class DynamoExtractorTest {

    @Before
    public void startFXToolkit() throws Exception {
        com.sun.javafx.application.PlatformImpl.startup(() -> assertTrue(Platform.isFxApplicationThread()));
    }

    @Ignore // Fail on travis
    public void testExtract() throws Exception {
        DynamoExtractor dynamoExtractor = new DynamoExtractor();
        AnchorPane anchorPane = new AnchorPane();
        EruAlarm insertedDynamo = new EruAlarm();

        List<Control> controls = new ArrayList<>();
        controls.add(new Button());
        controls.add(new CheckBox());
        controls.add(new DatePicker());
        controls.add(insertedDynamo);
        controls.forEach(control -> anchorPane.getChildren().add(control));

        List<Dynamo> dynamos = dynamoExtractor.extractFrom(anchorPane);
        assertTrue(dynamos.size() == 1);
        assertSame(dynamos.get(0), insertedDynamo);
    }
}