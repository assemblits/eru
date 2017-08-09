package com.marlontrujillo.eru.gui.toolbars.tag;

import com.marlontrujillo.eru.persistence.Container;
import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.tag.Tag;
import com.marlontrujillo.eru.util.PSVAlert;
import groovy.lang.Closure;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TagToolbarController extends AnchorPane implements Initializable {

    /* ********** Fields ********** */
    @FXML private Button            deleteGroupButton;
    @FXML private Button            addGroupButton;
    @FXML private Button            editTagButton;
    @FXML private Button            addTagButton;
    @FXML private Button            deleteTagButton;
    @FXML private TableView<Tag>    tagsTableView;
    @FXML private ChoiceBox<String> tagGroupsChoiceBox;
    private       TabPane           externalTabPane;

    /* ********** Constructors ********** */
    public TagToolbarController(TabPane externalTabPane) {
        assert externalTabPane != null;
        this.externalTabPane = externalTabPane;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TagToolbar.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            registerListeners();
            updateGroups();
            updateTableTags();
            addRightClickBehavior();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void addRightClickBehavior() {
    }

    /* ********** Initialization ********** */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set the columns auto-size width with binding
        tagsTableView.getColumns().get(0).prefWidthProperty().bind(tagsTableView.widthProperty().multiply(0.48));  // size in % for TagName
        tagsTableView.getColumns().get(1).prefWidthProperty().bind(tagsTableView.widthProperty().multiply(0.48));  // size in % for Value
    }

    private void registerListeners() {
        addGroupButton.setOnAction(value -> handleUserSelection("ADD_NEW_GROUP"));
        deleteGroupButton.setOnAction(value -> handleUserSelection("DELETE_GROUP"));
        editTagButton.setOnAction(value -> handleUserSelection("EDIT_SELECTED_TAG"));
        addTagButton.setOnAction(value -> handleUserSelection("ADD_NEW_TAG"));
        deleteTagButton.setOnAction(value -> handleUserSelection("DELETE_SELECTED_TAG"));
        tagGroupsChoiceBox.getSelectionModel().selectedItemProperty().addListener(observable -> updateTableTags());
    }

    private void handleUserSelection(final String PROPERTY) {
        switch (PROPERTY){
            case "ADD_NEW_GROUP":
                final String newGroupName = createNewGroup();

                if((newGroupName != null) && !newGroupName.isEmpty()){
                    tagGroupsChoiceBox.getItems().add(newGroupName);
                    tagGroupsChoiceBox.getSelectionModel().select(newGroupName);
                    updateTableTags();
                }
                break;
            case "DELETE_GROUP":
                PSVAlert deleteGroupAlert = new PSVAlert(Alert.AlertType.CONFIRMATION);
                deleteGroupAlert.setHeaderText("Do you really want to delete the selected group and All Tags associated to it?");
                Optional<ButtonType> result = deleteGroupAlert.showAndWait();
                if (result.get() == ButtonType.OK){
                    Container.getInstance().getTagsAgent().send(new Closure(this) {
                        void doCall(ObservableList<Tag> tags) {
                            for(Tag tag : tags){
//                                if(tag.getTagGroup().equals(tagGroupsChoiceBox.getValue())) tags.remove(tag);
                            }
                        }
                    });
                    updateGroups();
                    updateTableTags();
                }
                break;
            case "EDIT_SELECTED_TAG":
                Tag selectedTag = tagsTableView.getSelectionModel().getSelectedItem();
                if(selectedTag != null){
                    insertNewModificationTabInExternalTabPane(selectedTag);
                }
                break;
            case "ADD_NEW_TAG":
                // Create Tag within selected group
                Tag newTag = createNewTag();
                if(newTag != null){
                    insertNewModificationTabInExternalTabPane(newTag);
                }
                break;
            case "DELETE_SELECTED_TAG":
                Tag selectedTagToDelete = tagsTableView.getSelectionModel().getSelectedItem();
                PSVAlert deleteTagAlert = new PSVAlert(Alert.AlertType.CONFIRMATION);
                deleteTagAlert.setHeaderText("Do you really want to delete the selected tag?");
                Optional<ButtonType> res = deleteTagAlert.showAndWait();
                if (res.get() == ButtonType.OK){
                    Container.getInstance().getTagsAgent().send(new Closure(this) {
                        void doCall(ObservableList<Tag> tags) {
                            tags.remove(selectedTagToDelete);
                        }
                    });
                }
                updateGroups();
                updateTableTags();
                break;
        }
    }

    /* ********** Methods ********** */
    private Tag createNewTag(){
        final List<String> tagsNames = new ArrayList<>();
        Container.getInstance().getTagsAgent().getInstantVal().stream().forEach(tag -> tagsNames.add(tag.getName()));

        // Create name selection dialog pane
        Dialog<Tag> newTagDialog = new Dialog<>();
        newTagDialog.setTitle("New Tag");
        newTagDialog.setHeaderText("Please, set the basic configuration and press OK (or click title bar 'X' for cancel)");
        newTagDialog.setResizable(true);
        Label nameLabel = new Label("Name:");
        Label alertLabel = new Label("Tag name already exist.");
        alertLabel.setTextFill(Color.TRANSPARENT);
        TextField nameTextField = new TextField("NEW_TAG - " + System.currentTimeMillis());

        Label groupLabel = new Label("Group:");
        ChoiceBox<String> groupSelectionChoiceBox = new ChoiceBox<>(tagGroupsChoiceBox.getItems());
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);


        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (tagsNames.contains(newValue)) {
                newTagDialog.getDialogPane().lookupButton(buttonTypeOk).setDisable(true);
                alertLabel.setTextFill(Color.RED);
            } else {
                newTagDialog.getDialogPane().lookupButton(buttonTypeOk).setDisable(false);
                alertLabel.setTextFill(Color.TRANSPARENT);
            }
        });

        GridPane grid = new GridPane();
        grid.add(nameLabel, 1, 1);
        grid.add(nameTextField, 2, 1);
        grid.add(alertLabel, 3, 1);
        grid.add(groupLabel, 1, 2);
        grid.add(groupSelectionChoiceBox, 2, 2);

        newTagDialog.getDialogPane().setContent(grid);
        newTagDialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        newTagDialog.setResultConverter(param -> {
            if (param == buttonTypeOk) {
                Tag newTag = new Tag(nameTextField.getText());
//                newTag.setTagGroup(groupSelectionChoiceBox.getValue());
                return newTag;

            }
            return null;

        });

        Optional<Tag> result = newTagDialog.showAndWait();
        if(result.isPresent()){
            return result.get();
        } else {
            return null;
        }
    }

    private void insertNewModificationTabInExternalTabPane(Tag tagToModify){
        final String tabID          = tagToModify.getName();
        Boolean tabAlreadyExists    = false;

        for (Tab tab : externalTabPane.getTabs()){
            if(tab.getText().equals(tabID)){
                tabAlreadyExists = true;
                externalTabPane.getSelectionModel().select(tab);
            }
        }

        if(!tabAlreadyExists){
            TagModificationsController modificationsController = new TagModificationsController(tagToModify);
            Tab newTab = new Tab(tagToModify.getName());
            newTab.setContent(modificationsController);
            externalTabPane.getTabs().add(newTab);
            externalTabPane.getSelectionModel().select(newTab);
        }
    }

    private void updateTableTags() {
        final String selectedGroup = tagGroupsChoiceBox.getSelectionModel().getSelectedItem();

        tagsTableView.getItems().removeAll(tagsTableView.getItems());
        try {
            for(Tag tagInContainer : Container.getInstance().getTagsAgent().getVal()){
//                if(selectedGroup == null){
//                    tagsTableView.getItems().add(tagInContainer);
//                } else if(tagInContainer.getTagGroup().equals(selectedGroup)){
//                    tagsTableView.getItems().add(tagInContainer);
//                }
            }
        } catch (Exception e){
            LogUtil.logger.error("TagToolbar cannot get tag groups from container.", e);
        }
    }

    private void updateGroups(){
        tagGroupsChoiceBox.getItems().clear();
        try {
            for(Tag tagInContainer : Container.getInstance().getTagsAgent().getVal()){
//                if(!tagGroupsChoiceBox.getItems().contains(tagInContainer.getTagGroup())) tagGroupsChoiceBox.getItems().add(tagInContainer.getTagGroup());
            }
        } catch (Exception e){
            LogUtil.logger.error("TagToolbar cannot get tag groups from container.", e);
        }
    }

    private String createNewGroup(){
        // Create name selection dialog pane
        Dialog<String> newGroupDialog = new Dialog<>();
        newGroupDialog.setTitle("New Group");
        newGroupDialog.setHeaderText("Please, set new group name and press OK (or click title bar 'X' for cancel)");
        newGroupDialog.setResizable(true);

        Label nameLabel = new Label("Name:");
        TextField nameTextField = new TextField("NEW_GROUP - " + System.currentTimeMillis());
        Label alertLabel = new Label("Group name already exist.");
        alertLabel.setTextFill(Color.TRANSPARENT);

        GridPane grid = new GridPane();
        grid.add(nameLabel, 1, 1);
        grid.add(nameTextField, 2, 1);
        grid.add(alertLabel, 3, 1);

        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        newGroupDialog.getDialogPane().setContent(grid);
        newGroupDialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (tagGroupsChoiceBox.getItems().contains(newValue)) {
                newGroupDialog.getDialogPane().lookupButton(buttonTypeOk).setDisable(true);
                alertLabel.setTextFill(Color.RED);
            } else {
                newGroupDialog.getDialogPane().lookupButton(buttonTypeOk).setDisable(false);
                alertLabel.setTextFill(Color.TRANSPARENT);
            }
        });

        newGroupDialog.setResultConverter(param -> {
            if (param == buttonTypeOk) {
                return nameTextField.getText();
            }
            return null;
        });

        Optional<String> result = newGroupDialog.showAndWait();
        if(result.isPresent()){
            return result.get();
        } else {
            return null;
        }
    }
}
