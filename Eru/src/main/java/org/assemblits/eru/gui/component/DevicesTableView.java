package org.assemblits.eru.gui.component;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.assemblits.eru.entities.Address;
import org.assemblits.eru.entities.Connection;
import org.assemblits.eru.entities.Device;

import java.sql.Timestamp;

public class DevicesTableView extends EruTableView<Device> {

    private TableColumn<Device, String> groupColumn = new TableColumn<>("Group");
    private TableColumn<Device, String> nameColumn = new TableColumn<>("Name");
    private TableColumn<Device, Integer> unitIdentifierColumn = new TableColumn<>("ID");
    private TableColumn<Device, String> statusColumn = new TableColumn<>("Status");
    private TableColumn<Device, Integer> retriesColumn = new TableColumn<>("Retries");
    private TableColumn<Device, Boolean> enabledColumn = new TableColumn<>("Enabled");
    private TableColumn<Device, ObservableList<Address>> addressesColumn = new TableColumn<>("Addresses");
    private TableColumn<Device, Boolean> zeroBasedColumn = new TableColumn<>("Zero based");
    private TableColumn<Device, Connection> connectionColumn = new TableColumn<>("Connection");

    public DevicesTableView() {
        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(widthProperty().multiply(0.08));

        nameColumn.prefWidthProperty().bind(widthProperty().multiply(0.10));
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        unitIdentifierColumn.prefWidthProperty().bind(widthProperty().multiply(0.05));
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

        statusColumn.prefWidthProperty().bind(widthProperty().multiply(0.11));
        statusColumn.setCellValueFactory(param -> param.getValue().statusProperty());
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        retriesColumn.prefWidthProperty().bind(widthProperty().multiply(0.05));
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

        enabledColumn.prefWidthProperty().bind(widthProperty().multiply(0.05));
        enabledColumn.setCellValueFactory(param -> param.getValue().enabledProperty());
        enabledColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enabledColumn));

        addressesColumn.prefWidthProperty().bind(widthProperty().multiply(0.33));
        addressesColumn.setCellValueFactory(param -> {
            final ListProperty<Address> addressesInDevice = new SimpleListProperty<>(FXCollections.observableArrayList(param.getValue().getAddresses())); // Set initial values
            addressesInDevice.addListener((ListChangeListener<Address>) c -> {                                                                            // Set updater
                while (c.next()) {
                    if (c.wasPermutated()) {
                        // Nothing
                    } else if (c.wasUpdated()) {
                        // Nothing
                    } else {
                        for (Address remAddress : c.getRemoved()) {
                            remAddress.setOwner(null);
                            param.getValue().getAddresses().remove(remAddress);
                        }
                        for (Address addAddress : c.getAddedSubList()) {
                            if (addAddress.getOwner() != getSelectionModel().getSelectedItem()) {
                                addAddress.setOwner(getSelectionModel().getSelectedItem());
                            }
                            param.getValue().getAddresses().add(addAddress);
                        }
                    }
                }
            });
            return addressesInDevice;
        });
        addressesColumn.setCellFactory(param -> new AddressesTableCellForDeviceTable());

        zeroBasedColumn.prefWidthProperty().bind(widthProperty().multiply(0.08));
        zeroBasedColumn.setCellValueFactory(param -> param.getValue().zeroBasedProperty());
        zeroBasedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enabledColumn));

        connectionColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));
        connectionColumn.setCellValueFactory(param -> param.getValue().connectionProperty());


        getColumns().addAll(
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

        setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setTableMenuButtonVisible(true);
    }

    @Override
    public void addNewItem() {
        Device newDevice = new Device();
        newDevice.setName("New device");
        newDevice.setGroupName("Devices");
        getItems().add(newDevice);
        getSelectionModel().clearSelection();
        getSelectionModel().select(newDevice);
    }

    public void setDevicesAndConnections(ObservableList<Device> devices, ObservableList<Connection> connections) {
        super.setItems(devices);
        connectionColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(connections));
    }

    class AddressesTableCellForDeviceTable extends TableCell<Device, ObservableList<Address>> {

        @Override
        protected void updateItem(ObservableList<Address> item, boolean empty) {
            super.updateItem(item, empty);
            updateViewMode();
        }

        private void updateViewMode() {
            setGraphic(null);
            setText(null);
            if (isEditing()) {
                VBox box = new VBox();
                TableView<Address> addressesTableView = new TableView<>();
                addressesTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                TableColumn<Address, String> typeColumn = new TableColumn<>("Type");
                typeColumn.setCellValueFactory(param -> param.getValue().dataModelProperty().asString());
                typeColumn.prefWidthProperty().bind(addressesTableView.widthProperty().multiply(0.14));

                TableColumn<Address, String> addressColumn = new TableColumn<>("ID");
                addressColumn.setCellValueFactory(param -> param.getValue().networkIDProperty().asString());
                addressColumn.prefWidthProperty().bind(addressesTableView.widthProperty().multiply(0.12));

                TableColumn<Address, String> valueColumn = new TableColumn<>("Value");
                valueColumn.setCellValueFactory(param -> param.getValue().currentValueProperty().asString());
                valueColumn.prefWidthProperty().bind(addressesTableView.widthProperty().multiply(0.12));

                TableColumn<Address, String> statusColumn = new TableColumn<>("Status");
                statusColumn.setCellValueFactory(param -> param.getValue().statusProperty());
                statusColumn.prefWidthProperty().bind(addressesTableView.widthProperty().multiply(0.12));

                TableColumn<Address, Timestamp> timestampColumn = new TableColumn<>("Timestamp");
                timestampColumn.setCellValueFactory(param -> param.getValue().timestampProperty());
                timestampColumn.prefWidthProperty().bind(addressesTableView.widthProperty().multiply(0.45));

                addressesTableView.getColumns().addAll(typeColumn, addressColumn, valueColumn, statusColumn, timestampColumn);

                TextField startAddressTextField = new TextField();
                startAddressTextField.setPromptText("Start");
                TextField endAddressTextField = new TextField();
                endAddressTextField.setPromptText("End");
                ChoiceBox<Address.DataModel> typeChoiceBox = new ChoiceBox<>();

                if (getItem() != null) {
                    addressesTableView.getItems().addAll(getItem());
                    typeChoiceBox.getItems().addAll(Address.DataModel.values());
                }

                Button addAddressButton = new Button("Add");
                addAddressButton.setOnAction(event -> {
                    try {
                        final int firstAddress = Integer.parseInt(startAddressTextField.getText());
                        final int lastAddress = Integer.parseInt(endAddressTextField.getText());
                        if (firstAddress > lastAddress) throw new Exception("Last Address is minor than first.");
                        final Address.DataModel dataType = typeChoiceBox.getSelectionModel().getSelectedItem();

                        for (int i = firstAddress; i <= lastAddress; i++) {
                            boolean isAlreadyInTable = false;
                            for (Address address : addressesTableView.getItems()) {
                                if ((address.getNetworkID() == i) && (address.getDataModel().equals(dataType))) {
                                    isAlreadyInTable = true;
                                    break;
                                }
                            }
                            if (!isAlreadyInTable) {
                                Address newAddress = new Address();
                                newAddress.setNetworkID(i);
                                newAddress.setDataModel(dataType);
                                addressesTableView.getItems().add(newAddress);
                            }
                        }
                        // Clear the new addresses fields
                        startAddressTextField.clear();
                        endAddressTextField.clear();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                Button deleteAddressButton = new Button("Remove");
                deleteAddressButton.setOnAction(event -> {
                    final int CURRENT_INDEX = addressesTableView.getSelectionModel().getSelectedIndex();
                    addressesTableView.getItems().removeAll(addressesTableView.getSelectionModel().getSelectedItems());
                    addressesTableView.getSelectionModel().select(CURRENT_INDEX - 1);
                });

                Button okButton = new Button("OK");
                okButton.setOnAction(event -> commitEdit(addressesTableView.getItems()));
                okButton.setDefaultButton(true);

                ToolBar toolBar = new ToolBar(
                        startAddressTextField,
                        endAddressTextField,
                        typeChoiceBox,
                        new HBox(addAddressButton,
                                deleteAddressButton,
                                okButton));
                toolBar.setOrientation(Orientation.VERTICAL);

                box.getChildren().addAll(addressesTableView, toolBar);
                setGraphic(box);
            } else {
                if (getItem() != null) {
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
        public void commitEdit(ObservableList<Address> newValue) {
            super.commitEdit(newValue);
        }
    }
}
