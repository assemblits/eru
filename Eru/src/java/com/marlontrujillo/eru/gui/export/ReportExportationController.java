package com.marlontrujillo.eru.gui.export;

import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.tag.Tag;
import com.marlontrujillo.eru.persistence.Dao;
import com.marlontrujillo.eru.util.JpaUtil;
import com.marlontrujillo.eru.util.PSVAlert;
import com.marlontrujillo.eru.util.PdfReportCreator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.persistence.EntityManager;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * Title: ReportExportationController.java <br>
 * Company: Comelecinca Power Systems C.A.<br>
 *
 * @author Marlon Trujillo (MT)
 * @version 1.0
 * <br><br>
 * Changes:<br>
 * <ul>
 * <li> 2014-07-15 (MT) Creation </li>*
 * <ul>
 */
public class ReportExportationController extends AnchorPane implements Initializable {

    /* ********** Fields ********** */
    @FXML private ListView<Tag>     selectedTagsListView;
    @FXML private ListView<Tag>     availableTagsListView;
    @FXML private VBox              initDateVBox;
    @FXML private VBox              finalDateVBox;
    @FXML private Label             userMessageText;
    @FXML private HBox              messageHBox;
    @FXML private Tab               historianTab;
    @FXML private Button            addTagButton;
    @FXML private Button            deleteTagButton;
    @FXML private DatePicker        initTagDatePicker;
    @FXML private DatePicker        lastTagDatePicker;
    @FXML private CheckBox          checkBoxAllTags;
    @FXML private CheckBox          checkBoxTodayTags;
    @FXML private CheckBox          checkBoxRangeTags;
    @FXML private Button            exportButton;
    @FXML private Button            cancelButton;


    /* ********** Constructors ********** */
    public ReportExportationController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ReportExportation.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        registerListeners();
    }

    /* ********** Initialization ********** */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Fill the choice boxes
        try {

            EntityManager entityManager   = JpaUtil.getEntityManagerFactory().createEntityManager();
            Dao<Tag> tagDao= new Dao<>(entityManager, Tag.class);
            for(Tag t : tagDao.findEntities("name", Dao.Order.ASC)){
                if(t.getHistoricalEnabled()) availableTagsListView.getItems().add(t);
            }

        } catch (Exception e) {
            LogUtil.logger.error("Report exportation GUI cannot load", e);
        }

        // Customize the datePickers
        initTagDatePicker.setValue(LocalDate.of(1985,9,12));
        initTagDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isAfter(lastTagDatePicker.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                        long p = ChronoUnit.DAYS.between(item, lastTagDatePicker.getValue());
                        setTooltip(new Tooltip("You're about to export " + p + " days")
                        );
                    }
                };
            }
        });

        lastTagDatePicker.setValue(LocalDate.now());
        lastTagDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(initTagDatePicker.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                        long p = ChronoUnit.DAYS.between(initTagDatePicker.getValue(), item);
                        setTooltip(new Tooltip("You're about to export " + p + " days")
                        );
                    }
                };
            }
        });

    }

    private void registerListeners() {
        cancelButton.setOnAction(value -> closeStage());
        exportButton.setOnAction(value -> handleUserSelection("EXPORT"));
        addTagButton.setOnAction(value -> handleUserSelection("ADD_TAG_BUTTON"));
        deleteTagButton.setOnAction(value -> handleUserSelection("DELETE_TAG"));
        checkBoxAllTags.setOnAction(value -> handleUserSelection("DATE_ALL_TAGS"));
        checkBoxTodayTags.setOnAction(value -> handleUserSelection("DATE_TODAY_TAGS"));
        checkBoxRangeTags.setOnAction(value -> handleUserSelection("DATE_RANGE_TAGS"));
    }

    private void handleUserSelection(final String PROPERTY) {
        switch (PROPERTY){
            case "EXPORT":
                    exportHistoric();
                break;
            case "ADD_TAG_BUTTON":
                if (availableTagsListView.getSelectionModel().getSelectedItem() != null) {
                    selectedTagsListView.getItems().add(availableTagsListView.getSelectionModel().getSelectedItem());
                    availableTagsListView.getItems().remove(availableTagsListView.getSelectionModel().getSelectedItem());
                }
                break;
            case "DELETE_TAG":
                if(selectedTagsListView.getSelectionModel().getSelectedItem()!=null){
                    availableTagsListView.getItems().add(selectedTagsListView.getSelectionModel().getSelectedItem());
                    selectedTagsListView.getItems().remove(selectedTagsListView.getSelectionModel().getSelectedItem());
                }
                break;
            case "DATE_ALL_TAGS":
                if(checkBoxAllTags.isSelected()){
                    checkBoxRangeTags.setSelected(false);
                    checkBoxTodayTags.setSelected(false);
                    initDateVBox.setVisible(false);
                    finalDateVBox.setVisible(false);
                    initTagDatePicker.setValue(LocalDate.of(1985,9,12));
                    lastTagDatePicker.setValue(LocalDate.now());
                } else {
                    checkBoxAllTags.setSelected(true);
                }
                break;
            case "DATE_TODAY_TAGS":
                if (checkBoxTodayTags.isSelected()) {
                    checkBoxRangeTags.setSelected(false);
                    checkBoxAllTags.setSelected(false);
                    initDateVBox.setVisible(true);
                    finalDateVBox.setVisible(true);
                    initTagDatePicker.setDisable(true);
                    lastTagDatePicker.setDisable(true);
                    initTagDatePicker.setValue(LocalDate.now().atStartOfDay().toLocalDate());
                    lastTagDatePicker.setValue(LocalDate.now().plusDays(1));
                } else {
                    checkBoxTodayTags.setSelected(true);
                }
                break;
            case "DATE_RANGE_TAGS":
                if (checkBoxRangeTags.isSelected()) {
                    checkBoxAllTags.setSelected(false);
                    checkBoxTodayTags.setSelected(false);
                    initDateVBox.setVisible(true);
                    finalDateVBox.setVisible(true);
                    initTagDatePicker.setDisable(false);
                    lastTagDatePicker.setDisable(false);
                    initTagDatePicker.setValue(LocalDate.now());
                    lastTagDatePicker.setValue(LocalDate.now());
                }else{
                    checkBoxRangeTags.setSelected(true);
                }
                break;
            case "DATE_ALL_ALARMS":
                break;
            case "DATE_TODAY_ALARMS":
                break;
            case "DATE_RANGE_ALARMS":
                break;
        }
    }


    /* ********** Methods ********** */
    private void exportHistoric() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();

        // If is empty the selected list, or is not selected any checkbox Return!
        if((selectedTagsListView.getItems().isEmpty())){
            PSVAlert exportAllConfirmation = new PSVAlert(Alert.AlertType.CONFIRMATION);
            exportAllConfirmation.setTitle("The list is empty. Do you want to export only time stamp?");
            Optional<ButtonType> result = exportAllConfirmation.showAndWait();
            if (result.get() != ButtonType.OK) {
                LogUtil.logger.warn("The report is printing a empty list");
            } else {
                return;
            }
        } else if(!checkBoxAllTags.isSelected() & !checkBoxTodayTags.isSelected() & !checkBoxRangeTags.isSelected()) {
            PSVAlert exportAllConfirmation = new PSVAlert(Alert.AlertType.CONFIRMATION);
            exportAllConfirmation.setTitle("Please, select a tag date checkbox.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF File (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Export PDF Report");
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        // Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        // If 'cancel' button is pressed on the saveDatabase dialog, return...
        if(file == null) return;

        if (!file.getName().contains(".")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }

        if (checkBoxTodayTags.isSelected() || checkBoxRangeTags.isSelected()) {
            PdfReportCreator pdfReport = new PdfReportCreator();
            pdfReport.setTags(selectedTagsListView.getItems());
            pdfReport.setReportType(PdfReportCreator.Report.DATE_FILTER_TAGS);
            pdfReport.setInitDate(initTagDatePicker.getValue());
            pdfReport.setFinalDate(lastTagDatePicker.getValue());
            pdfReport.setFileName(file.getAbsolutePath());
            //pdfReport.setAuthor(Main.actualUser.getUserName()); TODO: User name specifications
            try {
                pdfReport.makePDF("Historic Report");
            } catch (Exception e) {
                PSVAlert errorWindow = new PSVAlert(Alert.AlertType.ERROR);
                errorWindow.setTitle(e.getMessage());
                LogUtil.logger.error("Error exporting historic.", e);
            }
        } else if (checkBoxAllTags.isSelected()) {
            PdfReportCreator pdfReport = new PdfReportCreator();
            pdfReport.setTags(selectedTagsListView.getItems());
            pdfReport.setReportType(PdfReportCreator.Report.ALL_TAGS);
            pdfReport.setInitDate(initTagDatePicker.getValue());
            pdfReport.setFinalDate(lastTagDatePicker.getValue());
            pdfReport.setFileName(file.getAbsolutePath());
            // pdfReport.setAuthor(Main.actualUser.getUserName()); TODO: User name again!
            try {
                pdfReport.makePDF("Complete Historic Report");
            } catch (Exception e) {
                PSVAlert errorWindow = new PSVAlert(Alert.AlertType.ERROR);
                errorWindow.setTitle(e.getMessage());
                LogUtil.logger.error("Error exporting historic.", e);
            }
        }

        closeStage();
    }


    private void closeStage(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
