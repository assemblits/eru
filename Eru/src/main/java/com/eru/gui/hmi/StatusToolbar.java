package com.eru.gui.hmi;

import com.eru.alarming.AlarmsController;
import com.eru.gui.ApplicationContextHolder;
import com.eru.gui.export.ReportExportationController;
import com.eru.scene.control.Display;
import com.eru.util.Constants;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mtrujillo on 3/21/2016.
 */
public class StatusToolbar extends VBox implements Initializable {

    private final Stage owner;
    public Text connectionStatusText;
    public Text lastMessageText;
    public Separator leftSeparator;
    public Separator rightSeparator;
    public Label titleLabel;
    public Display realKwInSyst;
    public Display nomKwInSyst;
    public Display reservKwInSyst;

    private ReportExportationController reportExportationController;

    public StatusToolbar(Stage owner) {
        this.owner = owner;
        reportExportationController = ApplicationContextHolder.getApplicationContext().getBean(ReportExportationController.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectionStatusText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("ONLINE")) {
                connectionStatusText.setFill(Paint.valueOf("GREEN"));
            } else {
                connectionStatusText.setFill(Paint.valueOf("RED"));
            }
        });
    }

    @FXML
    private void alarmTableButtonFired(ActionEvent actionEvent) {
        try {
            final Parent alarmsWindow = FXMLLoader.load(AlarmsController.class.getResource("Alarms.fxml"));
            final Stage alarmStage = new Stage();
            alarmStage.initStyle(StageStyle.TRANSPARENT);
            alarmStage.setTitle(Constants.SOFTWARE_NAME);
            alarmStage.setScene(new Scene(alarmsWindow, Color.TRANSPARENT));
            alarmStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void fullscreenMenuItemFired(ActionEvent actionEvent) {
        owner.setFullScreen(!owner.isFullScreen());
    }

    @FXML
    private void screenshotMenuItemFired(ActionEvent actionEvent) {
        WritableImage image = new WritableImage((int) owner.getScene().getWidth(), (int) owner.getScene().getHeight());
        owner.getScene().snapshot(image);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG File (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Save screenshot image");
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        try {
            File screenshotImageFile = fileChooser.showSaveDialog(owner);
            if (screenshotImageFile == null) return;
            if (!screenshotImageFile.getName().contains(".")) {
                screenshotImageFile = new File(screenshotImageFile.getAbsolutePath() + ".png");
            }
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", screenshotImageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exportExcelMenuItemFired(ActionEvent actionEvent) {
        final Stage exportationStage = new Stage();
        exportationStage.setScene(new Scene(reportExportationController));
        exportationStage.show();
    }

}
