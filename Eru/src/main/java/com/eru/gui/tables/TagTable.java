package com.eru.gui.tables;

import com.eru.entities.Address;
import com.eru.entities.Device;
import com.eru.gui.App;
import com.eru.entities.Tag;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by mtrujillo on 8/9/17.
 */
public class TagTable extends EruTable<Tag> {

    public TagTable(List<Tag> items) {
        super(items);

        // **** Columns **** //
        TableColumn<Tag, String> groupColumn             = new TableColumn<>("Group");
        TableColumn<Tag, String> nameColumn              = new TableColumn<>("Name");
        TableColumn<Tag, Tag> linkedTagColumn            = new TableColumn<>("Source");
        TableColumn<Tag, Boolean> enabledColumn          = new TableColumn<>("Enabled");
        TableColumn<Tag, String> descriptionColumn       = new TableColumn<>("Description");
        TableColumn<Tag, String> valueColumn             = new TableColumn<>("Value");
        TableColumn<Tag, Integer> decimalsColumn         = new TableColumn<>("Decimals");
        TableColumn<Tag, Tag.Type> tagTypeColumn         = new TableColumn<>("Type");
        TableColumn<Tag, String> statusColumn            = new TableColumn<>("Status");
        TableColumn<Tag, Address> addressColumn          = new TableColumn<>("Address");
        TableColumn<Tag, String> scriptColumn            = new TableColumn<>("Script");
        TableColumn<Tag, Integer> maskColumn             = new TableColumn<>("Mask");
        TableColumn<Tag, Double> scaleFactorColumn       = new TableColumn<>("Factor");
        TableColumn<Tag, Double> scaleOffsetColumn       = new TableColumn<>("Offset");
        TableColumn<Tag, Boolean> alarmEnabledColumn     = new TableColumn<>("Alarming");
        TableColumn<Tag, String> alarmColumn             = new TableColumn<>("Alarm");
        TableColumn<Tag, Boolean> alarmedColumn          = new TableColumn<>("Alarmed");
        TableColumn<Tag, Boolean> historianColumn        = new TableColumn<>("Historian");
        TableColumn<Tag, Timestamp> timestampTableColumn = new TableColumn<>("Timestamp");

        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.06));

        nameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.06));
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        linkedTagColumn.setCellValueFactory(param -> param.getValue().linkedTagProperty());
        linkedTagColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(getItems()));
        linkedTagColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));

        enabledColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        enabledColumn.setCellValueFactory(param -> param.getValue().enabledProperty());
        enabledColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enabledColumn));

        descriptionColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
        descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        valueColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        timestampTableColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
        timestampTableColumn.setCellValueFactory(param -> param.getValue().timestampProperty());
        timestampTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Timestamp>() {
            @Override
            public String toString(Timestamp object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Timestamp fromString(String string) {
                return null;
            }
        }));

        decimalsColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        decimalsColumn.setCellValueFactory(param -> param.getValue().decimalsProperty().asObject());
        decimalsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));
        decimalsColumn.setVisible(false);

        tagTypeColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.06));
        tagTypeColumn.setCellValueFactory(param -> param.getValue().typeProperty());
        tagTypeColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(Tag.Type.values())
        ));

        statusColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        statusColumn.setCellValueFactory(param -> param.getValue().statusProperty());
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        addressColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.12));
        addressColumn.setCellValueFactory(param -> param.getValue().linkedAddressProperty());
        addressColumn.setCellFactory(param -> new AddressesTableCellForTagTable());

        scriptColumn.setCellValueFactory(param -> param.getValue().scriptProperty());
        scriptColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        scriptColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        scriptColumn.setVisible(false);

        maskColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        maskColumn.setCellValueFactory(param -> param.getValue().maskProperty().asObject());
        maskColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));
        maskColumn.setVisible(false);

        scaleFactorColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.04));
        scaleFactorColumn.setCellValueFactory(param -> param.getValue().scaleFactorProperty().asObject());
        scaleFactorColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Double fromString(String string) {
                return Double.valueOf(string);
            }
        }));
        scaleFactorColumn.setVisible(false);

        scaleOffsetColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.04));
        scaleOffsetColumn.setCellValueFactory(param -> param.getValue().scaleOffsetProperty().asObject());
        scaleOffsetColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Double fromString(String string) {
                return Double.valueOf(string);
            }
        }));
        scaleOffsetColumn.setVisible(false);

        alarmEnabledColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        alarmEnabledColumn.setCellValueFactory(param -> param.getValue().alarmEnabledProperty());
        alarmEnabledColumn.setCellFactory(CheckBoxTableCell.forTableColumn(alarmEnabledColumn));
        alarmEnabledColumn.setVisible(false);

        alarmColumn.setCellValueFactory(param -> param.getValue().alarmScriptProperty());
        alarmColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        alarmColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        alarmColumn.setVisible(false);

        alarmedColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        alarmedColumn.setCellValueFactory(param -> param.getValue().alarmedProperty());
        alarmedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(alarmedColumn));

        historianColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        historianColumn.setCellValueFactory(param -> param.getValue().historicalEnabledProperty());
        historianColumn.setCellFactory(CheckBoxTableCell.forTableColumn(historianColumn));

        // **** General **** //
        this.getColumns().addAll(
                groupColumn,
                nameColumn,
                linkedTagColumn,
                enabledColumn,
                descriptionColumn,
                valueColumn,
                timestampTableColumn,
                decimalsColumn,
                tagTypeColumn,
                statusColumn,
                addressColumn,
                scriptColumn,
                maskColumn,
                scaleFactorColumn,
                scaleOffsetColumn,
                alarmEnabledColumn,
                alarmColumn,
                alarmedColumn,
                historianColumn
        );

        this.setEditable(true);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setTableMenuButtonVisible(true);
    }

    @Override
    public void addNewItem() {
        Tag newTag = new Tag();
        newTag.setName("New tag");
        newTag.setGroupName("Tags");
        this.items.add(newTag);
        this.getSelectionModel().clearSelection();
        this.getSelectionModel().select(newTag);

        // *******************************************************************************
        // Implemented to solve : https://javafx-jira.kenai.com/browse/RT-32091
        // When a new object is added to the table, a new filteredList has to be created
        // and the items updated, because the filteredList is non-editable. So, despite the
        // filtered List is setted to the tableview, a list is used in the background. The
        // filtered list is only used to be able to filter using the textToFilter.
        //
        //Wrap ObservableList into FilteredList
        super.filteredItems = new FilteredList<>(this.items);
        super.setItems(this.filteredItems);

        // Check if a textToFilter is setted
        if (super.textToFilter != null){
            setTextToFilter(textToFilter);
        }
        // *******************************************************************************
    }

    @Override
    public void setTextToFilter(StringProperty textToFilter) {
        textToFilter.addListener(observable ->
                this.filteredItems.setPredicate(device ->
                        (textToFilter.getValue() == null
                                || textToFilter.getValue().isEmpty()
                                || device.getName().startsWith(textToFilter.getValue())
                                || device.getGroupName().startsWith(textToFilter.getValue()))
                )
        );
    }
}

class AddressesTableCellForTagTable extends TableCell<Tag, Address> {

    @Override
    protected void updateItem(Address item, boolean empty) {
        super.updateItem(item, empty);
        updateViewMode();
    }

    private void updateViewMode() {
        setGraphic(null);
        setText(null);
        if(isEditing()){
            VBox box = new VBox();

            ListView<Device> deviceListView = new ListView<>();
            ListView<Address> addressListView = new ListView<>();

            deviceListView.getItems().addAll(App.getSingleton().getProject().getDevices());
            deviceListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue!= null) addressListView.getItems().addAll(FXCollections.observableArrayList(newValue.getAddresses()));
            });

            Button okButton = new Button("OK");
            okButton.setDefaultButton(true);
            okButton.setOnAction(event -> commitEdit(addressListView.getSelectionModel().getSelectedItem()));

            ToolBar toolBar = new ToolBar(okButton);

            box.getChildren().addAll(new HBox(deviceListView, addressListView), toolBar);
            setGraphic(box);
        } else {
            if(getItem() != null) {
                setText(getItem().toString());
            }
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        updateViewMode();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem() == null ? null : getItem().toString());
        setGraphic(null);
    }

    @Override
    public void commitEdit(Address newValue) {
        super.commitEdit(newValue);
    }
}