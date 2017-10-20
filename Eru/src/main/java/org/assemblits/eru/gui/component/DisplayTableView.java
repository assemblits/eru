package org.assemblits.eru.gui.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.entities.EruType;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.gui.model.ProjectModel;
import org.assemblits.eru.jfx.scenebuilder.SceneBuilderStarter;
import org.assemblits.eru.jfx.scenebuilder.SceneFxmlManager;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class DisplayTableView extends EruTableView<Display> {

    public DisplayTableView() {
        TableColumn<Display, Void> actionColumn = new TableColumn<>("Action");
        TableColumn<Display, String> groupColumn = new TableColumn<>("Group");
        TableColumn<Display, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Display, Display.StageType> stageTypeColumn = new TableColumn<>("Stage type");
        TableColumn<Display, Boolean> initialDisplayColumn = new TableColumn<>("Initial");

        actionColumn.setCellFactory(param -> new ShowActionCell<>(this::showDisplay));
        actionColumn.prefWidthProperty().bind(widthProperty().multiply(0.05));

        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(widthProperty().multiply(0.35));

        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.prefWidthProperty().bind(widthProperty().multiply(0.35));

        stageTypeColumn.prefWidthProperty().bind(widthProperty().multiply(0.15));
        stageTypeColumn.setCellValueFactory(param -> param.getValue().stageTypeProperty());
        stageTypeColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(Display.StageType.values())
        ));

        initialDisplayColumn.setCellValueFactory(param -> param.getValue().initialDisplayProperty());
        initialDisplayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(initialDisplayColumn));
        initialDisplayColumn.prefWidthProperty().bind(widthProperty().multiply(0.05));

        getColumns().addAll(
                actionColumn,
                groupColumn,
                nameColumn,
                stageTypeColumn,
                initialDisplayColumn
        );

        setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        addGraphicEditorMenuItem();
    }

    private void showDisplay(Integer displayIndexInTable) {
        try {
            log.info("Launching displays.");
            final Display display = getItems().get(displayIndexInTable);
            final SceneFxmlManager sceneFxmlManager = ApplicationContextHolder.getApplicationContext().getBean(SceneFxmlManager.class);
            final File sceneFxmlFile = sceneFxmlManager.createSceneFxmlFile(display);
            final URL fxmlFileUrl = sceneFxmlFile.toURI().toURL();
            final Parent displayNode = FXMLLoader.load(fxmlFileUrl);
            final Scene SCADA_SCENE = new Scene(displayNode);
            final Stage SCADA_STAGE = new Stage();
            display.setFxNode(displayNode);
            SCADA_STAGE.setScene(SCADA_SCENE);
            SCADA_STAGE.show();
        } catch (Exception e) {
            log.error("Error launching display", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error in SCADA launching process.");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void addGraphicEditorMenuItem() {
        final MenuItem displayEditor = new MenuItem("Edit graphic");
        final SceneBuilderStarter sceneBuilderStarter = ApplicationContextHolder.getApplicationContext().
                getBean(SceneBuilderStarter.class);
        displayEditor.setOnAction(event ->
                sceneBuilderStarter.startSceneBuilder(getSelectionModel().getSelectedItem()));
        getContextMenu().getItems().add(displayEditor);
    }

    @Override
    public void addNewItem() {
        getSelectionModel().clearSelection();

        Dialog<Display> dialog = new Dialog<>();
        dialog.setTitle("Create new display");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField displayName = new TextField();
        displayName.setPromptText("Name");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(displayName, 1, 0);

        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);
        displayName.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                Display newDisplay = new Display();
                newDisplay.setGroupName("Displays");
                newDisplay.setName(displayName.getText());
                return newDisplay;
            }
            return null;
        });

        Optional<Display> result = dialog.showAndWait();


        result.ifPresent(display -> {
            getItems().add(display);
            getSelectionModel().select(display);
        });
    }

    public void setDisplays(ObservableList<Display> displays) {
        setItems(displays);
    }

    class ShowActionCell<S> extends TableCell<S, Void> {
        private Button toggleButton;
        private ImageView buttonImageView = new ImageView(new Image(getClass().getResource("/images/show-display.png").toExternalForm()));

        public ShowActionCell(Consumer<Integer> onButtonPressedAction) {
            toggleButton = new Button();

            toggleButton.setGraphic(buttonImageView);
            toggleButton.setOnAction(event -> {
                log.info("launching");
                onButtonPressedAction.accept(getIndex());
            });
            setAlignment(Pos.CENTER);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            setGraphic(null);
            if (!empty) {
                setGraphic(toggleButton);
            }
        }
    }
}