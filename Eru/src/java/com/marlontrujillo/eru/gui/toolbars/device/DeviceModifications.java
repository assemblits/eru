package com.marlontrujillo.eru.gui.toolbars.device;

import com.marlontrujillo.eru.comm.connection.Connection;
import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.comm.device.Address;
import com.marlontrujillo.eru.comm.device.Device;
import com.marlontrujillo.eru.persistence.Container;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Created by mtrujillo on 19/05/17.
 */
public class DeviceModifications extends Tab implements Initializable {

    private Device device;

    @FXML private TableView<Address>            addressesTable;
    @FXML private Button                        addAddressButton;
    @FXML private Button                        deleteAddressButton;
    @FXML private TextField                     firstAddressTextField;
    @FXML private TextField                     lastAddressTextField;
    @FXML private TextField                     nameTextField;
    @FXML private TextField                     retriesTextField;
    @FXML private CheckBox                      enableCheckBox;
    @FXML private CheckBox                      zeroBasedCheckBox;
    @FXML private TextField                     unitIdentifierTextField;
    @FXML private ComboBox<Address.DataModel>   dataTypeComboBox;
    @FXML private ChoiceBox<Connection>         connectionsChoiceBox;

    public DeviceModifications(Device device) {
        this.device = device;
    }

    /* ********** Initialization ********** */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set the columns width auto size
        addressesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<Address, String> typeColumn = new TableColumn<>("Type");
//        typeColumn.setCellValueFactory(param -> param.getValue().getAddressPK().dataModelProperty().asString());
        TableColumn<Address, String> addressColumn = new TableColumn<>("Address");
//        addressColumn.setCellValueFactory(param -> param.getValue().getAddressPK().idProperty().asString());

        TableColumn<Address, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(param -> param.getValue().currentValueProperty().asString());
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        valueColumn.setOnEditCommit(t -> {
//            try {
//                Integer newValue = Integer.parseInt(t.getNewValue());
//                Address tableAddress = t.getTableView().getItems().get(t.getTablePosition().getRow());
//                tableAddress.setCurrentValue(newValue);
//                Address futureAddress = new Address();
//                futureAddress.setAddressPK(tableAddress.getAddressPK());
//                futureAddress.setCurrentValue(newValue);
//                ModbusSerialDeviceWriter modbusSerialDeviceWriter = new ModbusSerialDeviceWriter((ModbusSerialDevice) futureAddress.getAddressPK().getDevice(), futureAddress);
//                FieldBusCommunications.getInstance().addWriterParticipant(modbusSerialDeviceWriter);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
        valueColumn.setEditable(true);

        addressesTable.getColumns().set(0, typeColumn);
        addressesTable.getColumns().set(1, addressColumn);
        addressesTable.getColumns().set(2, valueColumn);

        addressesTable.getColumns().get(0).prefWidthProperty().bind(addressesTable.widthProperty().multiply(0.15));    // type column size
        addressesTable.getColumns().get(1).prefWidthProperty().bind(addressesTable.widthProperty().multiply(0.1));    // id column size
        addressesTable.getColumns().get(2).prefWidthProperty().bind(addressesTable.widthProperty().multiply(0.1));    // currentValue column size
        addressesTable.getColumns().get(3).prefWidthProperty().bind(addressesTable.widthProperty().multiply(0.4));   // status column size
        addressesTable.getColumns().get(4).prefWidthProperty().bind(addressesTable.widthProperty().multiply(0.2));    // timestamp column size

        connectionsChoiceBox.setItems(Container.getInstance().getConnectionsAgent().getInstantVal());
        // Set the Modbus Data Type
        dataTypeComboBox.getItems().addAll(Address.DataModel.values());

        // Identify the Tab
        setId(String.valueOf(device.getId()));
        setText(device.getName());

        // Activate listeners
        registerListeners();
        transferDeviceToUI(device);
    }

    private void registerListeners() {
        addAddressButton.setOnAction(value -> addAddresses());
        deleteAddressButton.setOnAction(value -> deleteAddresses());
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            setText(newValue);
            setId(newValue);
        });
    }

    /* ********** Private Methods ********** */
    public void handleSaveButtonAction(ActionEvent actionEvent) {
        Device tester = new Device();
        try {
            boolean transferredOK = transferUIToDevice(tester);
            if(transferredOK) {
                transferUIToDevice(device);
                firstAddressTextField.clear();
                lastAddressTextField.clear();
                if (!Container.getInstance().getDevicesAgent().getInstantVal().contains(device)){
                    Container.getInstance().getDevicesAgent().getVal().add(device);
                }
            } else {
                throw new Exception("Error saving " + device.getName());
            }
        } catch (Exception e) {
            LogUtil.logger.error(e);
        }
    }

    private void addAddresses() {
        try {
            // Check void fields
            if (firstAddressTextField.getText().equals("") || lastAddressTextField.getText().equals("")) {
                System.err.println("The Address configuration fields are empty.");
                return;
            }
            int firstAddress = Integer.parseInt(firstAddressTextField.getText());
            int lastAddress = Integer.parseInt(lastAddressTextField.getText());

            // Check valid input order
            if (firstAddress > lastAddress) {
                System.err.println("\"The field \\'Start\\' have to be less than \\'End\\'.\"");
                return;
            }

            // Check the Choice Box
            if (dataTypeComboBox.getSelectionModel().getSelectedItem() == null){
                System.err.println("Please, select a register type");
                return;
            }

            // Save the type of the new registers
            final Address.DataModel dataType = dataTypeComboBox.getSelectionModel().getSelectedItem();

            // Add the new address to table
            for (int i = firstAddress; i <= lastAddress; i++) {
                // Check if is in the table
                boolean isAlreadyInTable = false;
                for(Address address : addressesTable.getItems()){
//                    if((address.getAddressPK().getId() == i) && (address.getAddressPK().getDataModel().equals(dataType))){
//                        isAlreadyInTable = true;
//                        break;
//                    }
                }
                if(isAlreadyInTable){
                    continue;
                }

                // Configuring
                Address newAddress = new Address();
//                newAddress.getAddressPK().setId(i);
//                newAddress.getAddressPK().setDataModel(dataType);
//                addressesTable.getItems().add(newAddress);
            }

            // Clear the new addresses fields
            firstAddressTextField.clear();
            lastAddressTextField.clear();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void deleteAddresses() {
        final int CURRENT_INDEX = addressesTable.getSelectionModel().getSelectedIndex();
        addressesTable.getItems().removeAll(addressesTable.getSelectionModel().getSelectedItems());
        addressesTable.getSelectionModel().select(CURRENT_INDEX -1);
    }

    private void transferDeviceToUI(Device deviceInEdition){
        nameTextField.setText(deviceInEdition.getName());
        enableCheckBox.setSelected(deviceInEdition.getEnabled());
        unitIdentifierTextField.setText(Integer.toString(deviceInEdition.getUnitIdentifier()));
        retriesTextField.setText(String.valueOf(deviceInEdition.getRetries()));
        zeroBasedCheckBox.setSelected(deviceInEdition.isZeroBased());
        addressesTable.getItems().clear();
        addressesTable.getItems().addAll(deviceInEdition.getAddresses());
        Collections.sort(addressesTable.getItems());
        connectionsChoiceBox.getSelectionModel().select(deviceInEdition.getConnection());
    }

    private boolean transferUIToDevice(Device device){
        boolean transferResultOK = false;
        try {
            device.setName(nameTextField.getText());
            device.getAddresses().retainAll(addressesTable.getItems());
//            addressesTable.getItems().stream()
//                    .filter(addrInTable -> !device.getAddresses().contains(addrInTable))
//                    .forEach(newAddress -> {
//                        newAddress.getAddressPK().setDevice(device);
//                        device.getAddresses().add(newAddress);
//                    });
            // Overwrite the new device configuration
            device.setEnabled(enableCheckBox.isSelected());
            device.setZeroBased(zeroBasedCheckBox.isSelected());
            device.setConnection(connectionsChoiceBox.getValue());
            device.setRetries(Integer.parseInt(retriesTextField.getText()));
            device.setUnitIdentifier(Integer.parseInt(unitIdentifierTextField.getText()));
            transferResultOK = true;
        } catch (Exception e){
            LogUtil.logger.error(e);
        }
        return transferResultOK;
    }

}
