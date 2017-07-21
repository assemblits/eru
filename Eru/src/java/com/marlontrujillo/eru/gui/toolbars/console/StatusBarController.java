package com.marlontrujillo.eru.gui.toolbars.console;

import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.logger.LabelAppender;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mtrujillo on 9/2/2015.
 */
public class StatusBarController extends AnchorPane implements Initializable {

    static final Logger logger = LogUtil.logger;
    @FXML private Label label;

    public StatusBarController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("StatusBar.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LabelAppender.setLabel(label);
    }
}
