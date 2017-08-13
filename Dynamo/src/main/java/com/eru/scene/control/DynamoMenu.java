package com.eru.scene.control;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mtrujillo on 5/16/2016.
 */
public class DynamoMenu {
    static final double DEFAULT_HEIGHT           = 21.0;
    static final double DEFAULT_WIDTH            = 120.0;
    static final double DEFAULT_OPACITY          = 0.7;
    static final double OPACITY_ON_MOUSE_ENTERED = 1.0;

    private Dynamo               dynamo;
    private Menu                 menu;
    private List<DynamoMenuItem> menuItems;

    public DynamoMenu(DynamoMenuItem... menuItems) {
        this.menuItems = new ArrayList<>();
        addDefaultMenuItems();
        addMenuItems(menuItems);
    }

    private void addDefaultMenuItems() {
        this.menuItems.add(highlightMenuItem());
    }

    private DynamoMenuItem highlightMenuItem(){
        final String name = "Highlight";
        final String shape = "\"m 26.707589,487.91129 c -2.82272,0 -5.09375,2.27103 -5.09375,5.09375 l 0,33 c 0,2.82272 2.27103,5.09375 5.09375,5.09375 l 41.375,0 c 2.82272,0 5.09375,-2.27103 5.09375,-5.09375 l 0,-33 c 0,-2.82272 -2.27103,-5.09375 -5.09375,-5.09375 l -41.375,0 z m 55.78125,0 c -2.82272,0 -5.09375,2.27103 -5.09375,5.09375 l 0,33 c 0,2.82272 2.27103,5.09375 5.09375,5.09375 l 199.375001,0 c 2.82272,0 5.09375,-2.27103 5.09375,-5.09375 l 0,-33 c 0,-2.82272 -2.27103,-5.09375 -5.09375,-5.09375 l -199.375001,0 z m 47.406251,12.65625 c 0.39656,-0.009 0.79688,0.0123 1.21875,0.0312 l 12.40625,0 0,2.28125 -13.21875,0 c -0.42188,-0.0197 -0.80439,0.0266 -1.125,0.125 -0.32063,0.0985 -0.48313,0.35971 -0.5,0.8125 l 0,11.34375 c 0.0169,0.45281 0.17937,0.74532 0.5,0.84375 0.32061,0.0984 0.70312,0.14469 1.125,0.125 l 8.90625,0 c 0.42185,0.0197 0.80436,-0.0266 1.125,-0.125 0.3206,-0.0984 0.48311,-0.39094 0.5,-0.84375 l 0,-5.53125 -4.0625,0 0,-2.28125 8.09375,0 0,7.40625 c -0.0675,1.74654 -0.71754,2.80782 -2,3.1875 -1.2825,0.37968 -2.78129,0.54469 -4.46875,0.46875 l -7.28125,0 c -1.68749,0.0759 -3.18627,-0.0891 -4.46875,-0.46875 -1.28249,-0.37963 -1.96375,-1.44091 -2.03125,-3.18745 l 0,-10.53125 c 0.0675,-1.74652 0.74876,-2.8078 2.03125,-3.1875 0.96186,-0.28475 2.06033,-0.44027 3.25,-0.46875 z m 77.5,0 c 0.39656,-0.009 0.79688,0.0123 1.21875,0.0312 l 12.4375,0 0,2.28125 -13.21875,0 c -0.42188,-0.0197 -0.80439,0.0266 -1.125,0.125 -0.32063,0.0985 -0.48313,0.35971 -0.5,0.8125 l 0,11.34375 c 0.0169,0.45281 0.17937,0.74532 0.5,0.84375 0.32061,0.0984 0.70312,0.14469 1.125,0.125 l 8.90625,0 c 0.42185,0.0197 0.77311,-0.0266 1.09375,-0.125 0.3206,-0.0984 0.51436,-0.39094 0.53125,-0.84375 l 0,-5.53125 -4.0625,0 0,-2.28125 8.09375,0 0,7.40625 c -0.0675,1.74654 -0.74879,2.80782 -2.03125,3.1875 -1.2825,0.37968 -2.75004,0.54469 -4.4375,0.46875 l -7.3125,0 c -1.68749,0.0759 -3.15502,-0.0891 -4.4375,-0.46875 -1.28249,-0.37968 -1.96375,-1.44096 -2.03125,-3.1875 l 0,-10.53125 c 0.0675,-1.74652 0.74876,-2.8078 2.03125,-3.1875 0.96186,-0.28475 2.02908,-0.44027 3.21875,-0.46875 z m -114.625001,0.0312 4.0625,0 0,6.75 12.156251,0 0,-6.75 4.03125,0 0,17.8125 -4.03125,0 0,-8.78125 -12.156251,0 0,8.78125 -4.0625,0 0,-17.8125 z m 24.062501,0 4.03125,0 0,17.8125 -4.03125,0 0,-17.8125 z m 31.84375,0 4.0625,0 0,6.75 12.125,0 0,-6.75 4.0625,0 0,17.8125 -4.0625,0 0,-8.78125 -12.125,0 0,8.78125 -4.0625,0 0,-17.8125 z m 24.03125,0 4.0625,0 0,15.53125 14.84375,0 0,2.28125 -18.90625,0 0,-17.8125 z m 21.625,0 4.0625,0 0,17.8125 -4.0625,0 0,-17.8125 z m 31.875,0 4.03125,0 0,6.75 12.15625,0 0,-6.75 4.0625,0 0,17.8125 -4.0625,0 0,-8.78125 -12.15625,0 0,8.78125 -4.03125,0 0,-17.8125 z m 22.96875,0 22.40625,0 0,2.28125 -9.1875,0 0,15.53125 -4.0625,0 0,-15.53125 -9.15625,0 0,-2.28125 z m -201.781251,0.6563 c 0.93254,0 1.5625,0.55635 1.5625,1.375 l 0,8.25 c 0,0.68932 -0.62996,1.375 -1.5625,1.375 -0.93254,0 -1.5625,-0.54925 -1.5625,-1.375 l 0,-8.25 c 0,-0.6894 0.63807,-1.375 1.5625,-1.375 z m 0,13.75 c 0.93254,0 1.5625,0.55636 1.5625,1.375 0,0.81864 -0.79214,1.375 -1.5625,1.375 -0.93254,0 -1.5625,-0.54925 -1.5625,-1.375 0,-0.82575 0.63807,-1.375 1.5625,-1.375 z\"";
        final String style = "-fx-opacity: 0.7;" + "-fx-background-color: WHITE;" + "-fx-shape: " + shape + ";";
        final Region graphic = new Region();
        graphic.setStyle(style);
        graphic.setPrefHeight(DynamoMenu.DEFAULT_HEIGHT);
        graphic.setPrefWidth(DynamoMenu.DEFAULT_WIDTH);
        graphic.setOnMouseExited(event -> graphic.setOpacity(DynamoMenu.DEFAULT_OPACITY));
        graphic.setOnMouseEntered(event -> graphic.setOpacity(DynamoMenu.OPACITY_ON_MOUSE_ENTERED));
        DynamoMenuItem highlightMenuITem = new DynamoMenuItem();
        highlightMenuITem.setText(name);
        highlightMenuITem.setGraphic(graphic);
        highlightMenuITem.setOnAction(event -> dynamo.setHighlighted(!dynamo.isHighlighted()));
        return highlightMenuITem;
    }

    private void addMenuItems(DynamoMenuItem[] menuItems){
        Collections.addAll(this.menuItems, menuItems);
    }

    public void setDynamo(Dynamo dynamo) {
        this.dynamo = dynamo;
        this.menu = new Menu(dynamo, MouseButton.PRIMARY);
        for(DynamoMenuItem menuItem : menuItems){
            menuItem.setDynamo(dynamo);
            menu.getItems().add(menuItem);
        }
    }
}