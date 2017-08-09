package com.marlontrujillo.eru.gui.toolbars.tag;

import com.marlontrujillo.eru.persistence.Container;
import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.tag.Tag;
import com.marlontrujillo.eru.comm.device.Address;
import com.marlontrujillo.eru.comm.device.Device;
import com.marlontrujillo.eru.util.PSVAlert;
import groovy.lang.Closure;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.ResourceBundle;


public class TagModificationsController extends AnchorPane implements Initializable {

    /* ********** Fields ********** */
    private boolean                         isChangeApplied = false;
    private int                             currentMaskBase = 10;
    private ListView<String>                tagsListView    = new ListView<>(FXCollections.observableArrayList());

    @FXML private TextArea                  scriptTextArea;
    @FXML private TextArea                  alarmScriptTextArea;
    @FXML private CheckBox                  enableTagCheckBox;
    @FXML private TextField                 nameTextField;
    @FXML private Button                    saveButton;
    @FXML private CheckBox                  alarmEnableCheckBox;
    @FXML private TextField                 alarmGroupNameTextField;
    @FXML private TextField                 decimalsTextField;
    @FXML private TextField                 maskTextField;
    @FXML private TextField                 factorTextField;
    @FXML private TextField                 groupTextField;
    @FXML private CheckBox                  historicsEnableCheckBox;
    @FXML private TextArea                  descriptionTextArea;
    @FXML private ChoiceBox<Tag.TagType>    tagTypeChoiceBox;
    @FXML private ChoiceBox<String>         maskRadixChoiceBox;
    @FXML private HBox                      tagSourceHBox;
    @FXML private VBox                      devicesVBox;
    @FXML private VBox                      addressesVBox;
    private       ChoiceBox<String>         logicalConditionChoiceBox;
    private       TextField                 logicalThresholdTextField;

    // Selection lists: These lists are made of Strings to avoid modifications on the container. The setting controller
    // just parse (read) the lists and transfer the names.
    private final Tag tagInEdition;
    @FXML private ListView<String>          deviceListView;
    @FXML private ListView<String>          addressListView;

    /* ********** Constructor ********** */
    public TagModificationsController(final Tag tagInEdition){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TagModifications.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            registerListeners();
            this.tagInEdition = tagInEdition;
            loadGuiFromTag(tagInEdition);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /* ********** Initialization ********** */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            Container.getInstance().getTagsAgent().getVal().stream().forEach(tag -> tagsListView.getItems().add(tag.getName()));
            Container.getInstance().getDevicesAgent().getVal().stream().forEach(dev -> deviceListView.getItems().add(dev.getName()));
        } catch (Exception e){
            LogUtil.logger.error("Tag toolbar cannot get the tags from the agent." , e);
        }

        logicalConditionChoiceBox   = new ChoiceBox<>(FXCollections.observableArrayList("<", "<=", ">", ">=", "=="));
        logicalThresholdTextField   = new TextField();

        tagTypeChoiceBox.getItems().addAll(Tag.TagType.values());
        tagTypeChoiceBox.getSelectionModel().select(Tag.TagType.INPUT);
        maskRadixChoiceBox.getSelectionModel().select(2);
        tagTypeScreenConfig(Tag.TagType.INPUT);
    }

    private void registerListeners() {
        enableTagCheckBox.setOnAction(value -> handleUserSelection("ENABLE_TAG"));
        deviceListView.getSelectionModel().selectedItemProperty().addListener(value -> handleUserSelection("DEVICE_SELECTED"));
        tagTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(value -> handleUserSelection("TAG_TYPE"));
        alarmEnableCheckBox.setOnAction(value -> handleUserSelection("ALARM_ENABLE"));
        maskRadixChoiceBox.showingProperty().addListener(value -> handleUserSelection("MASK_RADIX"));
        saveButton.setOnAction(value -> handleUserSelection("SAVE_TO_DB"));
    }

    /* ********** Methods ********** */
    private void handleUserSelection(final String PROPERTY) {
        switch (PROPERTY) {
            case "ENABLE_TAG":
                disableTagEditor(!enableTagCheckBox.isSelected());
                break;
            case "DEVICE_SELECTED":
                final String selecteDevice = deviceListView.getSelectionModel().getSelectedItem();
                if(selecteDevice != null){
                    addressListView.getItems().clear();
                    try{
                        Container.getInstance().getDevicesAgent().getVal().stream()
                                .filter(dev  -> dev.getName().equals(selecteDevice))
                                .forEach(dev -> dev.getAddresses().stream().forEach(address -> addressListView.getItems().add(String.valueOf(address.getAddressPK().toString()))));
                        Collections.sort(addressListView.getItems());
                    } catch (Exception e){
                        LogUtil.logger.error("Tag toolbar cannot get the addresses of " + selecteDevice , e);
                    }
                }
                break;
            case "TAG_TYPE":
                if(tagTypeChoiceBox.getSelectionModel().getSelectedItem() != null){
                    tagTypeScreenConfig(tagTypeChoiceBox.getSelectionModel().getSelectedItem());
                }
                break;
            case "ALARM_ENABLE":
                if (alarmEnableCheckBox.isSelected()) {
                    alarmGroupNameTextField.setDisable(false);
                    alarmScriptTextArea.setDisable(false);
                } else {
                    alarmGroupNameTextField.setDisable(true);
                    alarmScriptTextArea.setDisable(true);
                }
                break;
            case "SAVE_TO_DB":
                saveChanges();
                break;
            case "MASK_RADIX":
                if(maskTextField.getText() != null && !maskTextField.getText().isEmpty()){
                    final String stringMaskRadix = maskRadixChoiceBox.getSelectionModel().getSelectedItem();
                    final String stringMaskValue = maskTextField.getText();
                    switch (stringMaskRadix){
                        case "Hex":
                            maskTextField.setText(Integer.toString(Integer.parseInt(stringMaskValue, currentMaskBase), 16));
                            currentMaskBase = 16;
                            break;
                        case "Oct":
                            maskTextField.setText(Integer.toString(Integer.parseInt(stringMaskValue, currentMaskBase), 8));
                            currentMaskBase = 8;
                            break;
                        case "Dec":
                            maskTextField.setText(Integer.toString(Integer.parseInt(stringMaskValue, currentMaskBase), 10));
                            currentMaskBase = 10;
                            break;
                        case "Bin":
                            maskTextField.setText(Integer.toString(Integer.parseInt(stringMaskValue, currentMaskBase), 2));
                            currentMaskBase = 2;
                            break;
                    }
                }
                break;
        }
    }

    private void saveChanges() {
        isChangeApplied     = false;

        // App parameters
        boolean isValid = isDescriptionRight();

        // Specific parameters
        if(isValid){
            switch (tagTypeChoiceBox.getSelectionModel().getSelectedItem()) {
                case INPUT:
                    isValid = isFactorRight() & isDecimalsRight();
                    if(addressListView.getSelectionModel().isEmpty()){
                        isValid = false;
                        showInformationAlertPane("Please, select the input address");
                    }
                    break;
                case MASK:
                    isValid = isTagMaskRight();
                    break;
                case MATH:
                    if(scriptTextArea.getText().isEmpty()){
                        isValid = false;
                        showInformationAlertPane("Please, write the tag script.");
                    }
                    break;
                case STATUS:
                    if(tagsListView.getSelectionModel().isEmpty()){
                        isValid = false;
                        showInformationAlertPane("Please, select the tag source.");
                    }
                    if(scriptTextArea.getText().isEmpty()){
                        isValid = false;
                        showInformationAlertPane("Please, write the tag script.");
                    }
                    break;
                case OUTPUT:
                    break;
                case LOGICAL:
                    if(tagsListView.getSelectionModel().isEmpty()){
                        isValid = false;
                        showInformationAlertPane("Please, select the tag source.");
                    }
                    if(logicalThresholdTextField.getText().isEmpty()){
                        isValid = false;
                        showInformationAlertPane("Please, select the logical threshold.");
                    }
                    if(!isThresholdRight()){
                        isValid = false;
                        showInformationAlertPane("Please, select the logical threshold.");
                    }
                    break;
            }
        }


        if (isValid) {
            try {
                Container.getInstance().getTagsAgent().sendAndWait(new Closure(this) {
                    void doCall(ObservableList<Tag> tags) {
                        // Check if the tagInEdition exists on Database
                        Boolean isANewTag = true;

                        for(Tag tagInContainer : tags){
                            if(tagInContainer.getName().equals(nameTextField.getText())){
                                isANewTag = false;
                            }
                        }

                        tagInEdition.setEnabled(enableTagCheckBox.isSelected());
                        tagInEdition.setName(nameTextField.getText());
                        tagInEdition.setTagType(tagTypeChoiceBox.getSelectionModel().getSelectedItem());

                        if(maskTextField.getText() != null && !maskTextField.getText().isEmpty()){
                            tagInEdition.setMask(Integer.parseInt(maskTextField.getText(), currentMaskBase));
                        }

                        tagInEdition.setScaleFactor(factorTextField.getText() == null ? 1.0 : Double.parseDouble(factorTextField.getText()));
                        tagInEdition.setDecimals(decimalsTextField.getText() == null ? 2 : Integer.parseInt(decimalsTextField.getText()));
                        tagInEdition.setDescription(descriptionTextArea.getText() == null ? "" : descriptionTextArea.getText());
                        tagInEdition.setScript(scriptTextArea.getText() == null ? "" : scriptTextArea.getText());

                        tagInEdition.setHistoricalEnabled(historicsEnableCheckBox.isSelected());
                        tagInEdition.setTimestamp(Timestamp.from(Instant.now()));

                        switch (tagTypeChoiceBox.getSelectionModel().getSelectedItem()) {
                            case OUTPUT:
                            case INPUT:
                                // Searching address selected
                                try {
                                    final Tag finalTagInEdition = tagInEdition;
                                    Container.getInstance().getDevicesAgent().getVal().stream()
                                            .filter(dev -> dev.getName().equals(deviceListView.getSelectionModel().getSelectedItem()))
                                            .forEach(dev -> {
                                                for (Address addr : dev.getAddresses()) {
                                                    if (addr.getAddressPK().toString().equals(addressListView.getSelectionModel().getSelectedItem())) {
                                                        finalTagInEdition.setAddress(addr);
                                                    }
                                                }
                                            });
                                } catch (InterruptedException e) {
                                    LogUtil.logger.error("Tag toolbar cannot get the addresses of " + tagInEdition, e);
                                }

                                break;
                            case MASK:
                                tagInEdition.setTagSourceName(tagsListView.getSelectionModel().getSelectedItem());
                                break;
                            case MATH:
                                tagInEdition.setTagSourceName(tagsListView.getSelectionModel().getSelectedItem());
                                break;
                            case STATUS:
                                tagInEdition.setTagSourceName(tagsListView.getSelectionModel().getSelectedItem());
                                break;
                            case LOGICAL:
                                break;
                        }

                        tagInEdition.setAlarmEnabled(alarmEnableCheckBox.isSelected());
                        if(alarmGroupNameTextField.getText() != null && (!alarmGroupNameTextField.getText().isEmpty())){
//                            tagInEdition.setAlarmGroupName(alarmGroupNameTextField.getText());
                        }
                        if(alarmScriptTextArea.getText() != null && (!alarmScriptTextArea.getText().isEmpty())){
                            tagInEdition.setAlarmScript(alarmScriptTextArea.getText());
                        }

                        if (isANewTag) {
                            tags.add(tagInEdition);
                            LogUtil.logger.info("Tag " + tagInEdition + " created.");
                        } else {
                            LogUtil.logger.info("Tag " + tagInEdition + " modified.");
                        }
                        isChangeApplied = true;
                    }
                });
            } catch (InterruptedException e) {
                LogUtil.logger.error("An Error saving changes on " + nameTextField.getText(), e);
            }
        }
    }

    private void loadGuiFromTag(Tag tagInEdition) {
        nameTextField.setDisable(true);

        enableTagCheckBox.setSelected(tagInEdition.getEnabled());
        disableTagEditor(!tagInEdition.getEnabled());

        groupTextField.setText(tagInEdition.getGroupName() == null ? "" : tagInEdition.getGroupName());
        nameTextField.setText(tagInEdition.getName() == null ? "" : tagInEdition.getName());
        descriptionTextArea.setText(tagInEdition.getDescription() == null ? "" : tagInEdition.getDescription());
        maskTextField.setText(Integer.toString(tagInEdition.getMask()));
        tagTypeChoiceBox.getSelectionModel().select(tagInEdition.getTagType() == null ? Tag.TagType.INPUT : tagInEdition.getTagType());
        factorTextField.setText(Double.toString(tagInEdition.getScaleFactor()));
        decimalsTextField.setText(Integer.toString(tagInEdition.getDecimals()));
        scriptTextArea.setText(tagInEdition.getScript() == null ? "" : tagInEdition.getScript());

        alarmEnableCheckBox.setSelected(tagInEdition.getAlarmEnabled());
        alarmGroupNameTextField.setDisable(!tagInEdition.getAlarmEnabled());
        alarmScriptTextArea.setDisable(!tagInEdition.getAlarmEnabled());
        alarmScriptTextArea.setText(tagInEdition.getAlarmScript() == null ? "" : tagInEdition.getAlarmScript());

        historicsEnableCheckBox.setSelected(tagInEdition.getHistoricalEnabled());

        tagTypeScreenConfig(tagInEdition.getTagType() == null ? Tag.TagType.INPUT : tagInEdition.getTagType());

        if((tagInEdition.getAddress() != null) && (tagInEdition.getAddress().getAddressPK().getDevice() != null)){
            final Device tagDevice = tagInEdition.getAddress().getAddressPK().getDevice();
            deviceListView.getSelectionModel().select(tagDevice.getName());
            tagDevice.getAddresses().stream().forEach(addr -> addressListView.getItems().add(addr.getAddressPK().toString()));
            Collections.sort(addressListView.getItems());
            addressListView.getSelectionModel().select(tagInEdition.getAddress().getAddressPK().toString());
        }

        if(tagInEdition.getTagSourceName() != null) {
            for(String tagSourceName : tagsListView.getItems()){
                if(tagSourceName.equals(tagInEdition.getTagSourceName())){
                    tagsListView.getSelectionModel().select(tagSourceName);
                }
            }
        }
    }

    private void disableTagEditor(boolean isDisable){
        tagTypeChoiceBox.setDisable(isDisable);
        deviceListView.setDisable(isDisable);
        descriptionTextArea.setDisable(isDisable);
        addressListView.setDisable(isDisable);
        factorTextField.setDisable(isDisable);
        decimalsTextField.setDisable(isDisable);
        maskTextField.setDisable(isDisable);
        tagsListView.setDisable(isDisable);
        scriptTextArea.setDisable(isDisable);
        alarmEnableCheckBox.setDisable(isDisable);
        alarmGroupNameTextField.setDisable(isDisable);

        if(alarmEnableCheckBox.isDisabled()){
            alarmGroupNameTextField.setDisable(true);
        }else if(alarmEnableCheckBox.isSelected()){
            alarmGroupNameTextField.setDisable(false);
        }
        historicsEnableCheckBox.setDisable(isDisable);
    }

    private void tagTypeScreenConfig(Tag.TagType tagType){
        switch (tagType) {
            case INPUT:
                tagSourceHBox.getChildren().setAll(deviceListView, addressListView);
                factorTextField.setDisable(false);
                decimalsTextField.setDisable(false);
                scriptTextArea.setDisable(true);
                addressListView.setDisable(false);
                maskTextField.setDisable(true);
                maskRadixChoiceBox.setDisable(true);
                deviceListView.setDisable(false);
                break;
            case OUTPUT:
                factorTextField.setDisable(true);
                decimalsTextField.setDisable(true);
                maskTextField.setDisable(true);
                scriptTextArea.setDisable(true);
                addressListView.setDisable(false);
                maskRadixChoiceBox.setDisable(true);
                deviceListView.setDisable(false);
                break;
            case MATH:
                tagSourceHBox.getChildren().setAll(tagsListView);
                factorTextField.setDisable(false);
                decimalsTextField.setDisable(false);
                scriptTextArea.setDisable(false);
                scriptTextArea.setPromptText("Mathematical scripts. Example: \nG2_GENFREQ.getCurrentValue() * 120.0 / 4.0");
                maskTextField.setDisable(true);
                maskRadixChoiceBox.setDisable(true);
                addressListView.setDisable(true);
                deviceListView.setDisable(true);
                break;
            case STATUS:
                tagSourceHBox.getChildren().setAll(tagsListView);
                factorTextField.setDisable(true);
                decimalsTextField.setDisable(true);
                scriptTextArea.setDisable(false);
                scriptTextArea.setPromptText("Status scripts. Example: \n1.0=ON,2.0=OFF,3.0=NONE");
                maskTextField.setDisable(true);
                maskRadixChoiceBox.setDisable(true);
                addressListView.setDisable(false);
                deviceListView.setDisable(false);
                break;
            case MASK:
                tagSourceHBox.getChildren().setAll(tagsListView);
                factorTextField.setDisable(false);
                decimalsTextField.setDisable(true);
                scriptTextArea.setDisable(true);
                addressListView.setDisable(true);
                maskTextField.setDisable(false);
                maskRadixChoiceBox.setDisable(false);
                deviceListView.setDisable(true);
                break;
            case LOGICAL:
                tagSourceHBox.getChildren().setAll(tagsListView, logicalConditionChoiceBox, logicalThresholdTextField);
                factorTextField.setDisable(true);
                decimalsTextField.setDisable(true);
                scriptTextArea.setDisable(true);
                addressListView.setDisable(true);
                maskTextField.setDisable(true);
                maskRadixChoiceBox.setDisable(true);
                deviceListView.setDisable(true);
                break;
        }
    }

    private boolean isFactorRight() {
//        boolean b = UserInputsValidator.isFactorValid(factorTextField.getText());
//        if (!b) {
//            showInformationAlertPane("Error: Incorrect factor");
//        }
//        return b;
        return true;
    }

    private boolean isDecimalsRight(){
//        boolean b = UserInputsValidator.isDecimalsValid(decimalsTextField.getText());
//        if (!b) {
//            showInformationAlertPane("Error: Incorrect decimals input.");
//        }
//        return b;
        return true;
    }

    private boolean isTagMaskRight() {
        boolean isRight = false;
        if(maskTextField.getText() != null && !maskTextField.getText().isEmpty()){
            final String stringMaskRadix = maskRadixChoiceBox.getSelectionModel().getSelectedItem();
            final String stringMaskValue = this.maskTextField.getText();
            switch (stringMaskRadix){
                case "Hex":
                    try{
                        Integer.parseInt(stringMaskValue, 16);
                        isRight = true;
                    } catch (Exception e){
                        showInformationAlertPane(e.getLocalizedMessage());
                        isRight = false;
                    }
                    break;
                case "Oct":
                    try{
                        Integer.parseInt(stringMaskValue, 8);
                        isRight = true;
                    } catch (Exception e){
                        showInformationAlertPane(e.getLocalizedMessage());
                        isRight = false;
                    }
                    break;
                case "Dec":
                    try{
                        Integer.parseInt(stringMaskValue, 10);
                        isRight = true;
                    } catch (Exception e){
                        showInformationAlertPane(e.getLocalizedMessage());
                        isRight = false;
                    }
                    break;
                case "Bin":
                    try{
                        Integer.parseInt(stringMaskValue, 2);
                        isRight = true;
                    } catch (Exception e){
                        showInformationAlertPane(e.getLocalizedMessage());
                        isRight = false;
                    }
                    break;
            }
        }

        return isRight;
    }

    private boolean isDescriptionRight() {
//        boolean b = UserInputsValidator.isDescriptionValid(descriptionTextArea.getText());
//        if (!b) {
//            showInformationAlertPane("Error: Wrong Description. Only 30 characters allowed.");
//        }
//        return b;
        return true;
    }

    private boolean isThresholdRight(){
        try {
            Integer.parseInt(logicalThresholdTextField.getText());
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private void showInformationAlertPane(final String information){
        PSVAlert alert = new PSVAlert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Information Alert");
        alert.setContentText(information);
        alert.show();
    }
}