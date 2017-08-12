package com.eru.scene.control;

import javafx.scene.control.MenuItem;

/**
 * Created by mtrujillo on 5/22/2016.
 */
public class DynamoMenuItem extends MenuItem{
    private Dynamo dynamo;

    public Dynamo getDynamo() {
        return dynamo;
    }
    public void setDynamo(Dynamo dynamo) {
        this.dynamo = dynamo;
    }
}
