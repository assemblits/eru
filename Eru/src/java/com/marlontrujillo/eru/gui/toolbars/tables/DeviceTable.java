package com.marlontrujillo.eru.gui.toolbars.tables;

import com.marlontrujillo.eru.comm.connection.Connection;
import com.marlontrujillo.eru.comm.device.Address;
import com.marlontrujillo.eru.comm.device.Device;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Created by mtrujillo on 8/8/17.
 */
public class DeviceTable extends EruTable<Device> {

    public DeviceTable(List<Device> items) {
        super(items);

        // **** Columns **** //
        TableColumn<Device, String> groupColumn             = new TableColumn<>("Group");
        TableColumn<Device, String> nameColumn              = new TableColumn<>("Name");
        TableColumn<Device, Integer> unitIdentifierColumn   = new TableColumn<>("ID");
        TableColumn<Device, String> statusColumn            = new TableColumn<>("Status");
        TableColumn<Device, Integer> retriesColumn          = new TableColumn<>("Retries");
        TableColumn<Device, Boolean> enabledColumn          = new TableColumn<>("Enabled");
        TableColumn<Device, List<Address>> addressesColumn  = new TableColumn<>("Addresses");
        TableColumn<Device, Boolean> zeroBasedColumn        = new TableColumn<>("Zero based");
        TableColumn<Device, Connection> connectionColumn    = new TableColumn<>("Connection");

        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.10));

        nameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.12));
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        unitIdentifierColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        unitIdentifierColumn.setCellValueFactory(param -> param.getValue().unitIdentifierProperty().asObject());
        unitIdentifierColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));

        statusColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.11));
        statusColumn.setCellValueFactory(param -> param.getValue().statusProperty());
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        retriesColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.11));
        retriesColumn.setCellValueFactory(param -> param.getValue().retriesProperty().asObject());
        retriesColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));

        enabledColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.10));
        enabledColumn.setCellValueFactory(param -> param.getValue().enabledProperty());
        enabledColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enabledColumn));

        addressesColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
        addressesColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getAddresses()));
        addressesColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<List<Address>>() {
            @Override
            public String toString(List<Address> object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public List<Address> fromString(String string) {
                return null; // TODO
            }
        }));

        zeroBasedColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.12));
        zeroBasedColumn.setCellValueFactory(param -> param.getValue().enabledProperty());
        zeroBasedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enabledColumn));

        connectionColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.14));
        connectionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getConnection()));
        connectionColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Connection>() {
            @Override
            public String toString(Connection object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Connection fromString(String string) {
                return null; // TODO
            }
        }));

        // **** General **** //
        this.getColumns().addAll(
                groupColumn,
                nameColumn,
                unitIdentifierColumn,
                statusColumn,
                retriesColumn,
                enabledColumn,
                addressesColumn,
                zeroBasedColumn,
                connectionColumn
        );

        this.setEditable(true);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @Override
    public void addNewItem() {
        Device newDevice = new Device();
        newDevice.setName("New device");
        newDevice.setGroupName("Devices");
        this.items.add(newDevice);
        this.getSelectionModel().clearSelection();
        this.getSelectionModel().select(newDevice);

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
