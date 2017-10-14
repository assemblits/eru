package org.assemblits.eru.gui.dynamo;

import javafx.scene.Node;
import javafx.scene.Parent;
import org.assemblits.eru.gui.dynamo.base.Dynamo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marlontrujillo1080 on 10/13/17.
 */
public class DynamoExtractor {

    public List<Dynamo> extractFrom(Parent parent) {
        List<Dynamo> dynamos = new ArrayList<>();
        for (Node innerNode : parent.getChildrenUnmodifiable()) {
            if (innerNode instanceof Dynamo) {
                Dynamo dynamo = (Dynamo) innerNode;
                dynamos.add(dynamo);
            }
        }
        return dynamos;
    }

}
