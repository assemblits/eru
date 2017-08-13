package com.eru.util;

import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Created by mtrujillo on 10/4/2015.
 */
public class PSVStageUtil extends Stage {

    public static enum Style {STANDARD_NO_BLOCKS, STANDARD_BLOCKER}

    public PSVStageUtil() {
        this(null);
    }

    public PSVStageUtil(Window owner){
        this(owner, Style.STANDARD_BLOCKER);
    }

    public PSVStageUtil(Window owner, Style style) {
        setPSVFormat(this, owner, style);
    }

    public static void setPSVFormat(Stage stage,Window owner, Style style){
        setPSVTitleAndIcon(stage);
        stage.initOwner(owner);
        switch (style) {
            case STANDARD_NO_BLOCKS:
                stage.initModality(Modality.NONE);
                break;
            case STANDARD_BLOCKER:
                stage.initModality(Modality.WINDOW_MODAL);
                break;
        }
    }

    public static void setPSVDesignerTitleAndIcon(Stage stage){
        final Image psvLogoImage = new Image(PSVStageUtil.class.getResource("Logo_Designer.png").toExternalForm());
        stage.setTitle(Constants.SOFTWARE_NAME + " Designer");
        stage.getIcons().add(psvLogoImage);
    }

    public static void setPSVTitleAndIcon(Stage stage){
        final Image psvLogoImage = new Image(PSVStageUtil.class.getResource("Logo.png").toExternalForm());
        stage.setTitle(Constants.SOFTWARE_NAME);
        stage.getIcons().add(psvLogoImage);
    }
}
