package com.eru.alarming;

import com.eru.dolphin.ClientStartupService;
import com.eru.util.Commands;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.opendolphin.binding.JavaFxUtil;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttributeWrapper;

import java.net.URL;
import java.util.ResourceBundle;

import static com.eru.util.DolphinConstants.*;

/**
 * Created by mtrujillo on 22/05/14.
 */
public class AlarmsController implements Initializable {
    @FXML private TableView<PresentationModel> table;

    public AlarmsController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addTableCustomCells();
        adjustTableSize();
        table.setItems(ClientStartupService.getInstance().getAlarmsPMObservableList());
    }

    private void adjustTableSize() {
        table.getColumns().get(0).prefWidthProperty().bind(table.widthProperty().multiply(0.18));  // size in % for date
        table.getColumns().get(1).prefWidthProperty().bind(table.widthProperty().multiply(0.60));  // size in % for description
        table.getColumns().get(2).prefWidthProperty().bind(table.widthProperty().multiply(0.09));  // size in % for group
        table.getColumns().get(3).prefWidthProperty().bind(table.widthProperty().multiply(0.10));  // size in % for ack
    }

    private void addTableCustomCells() {
        table.getColumns().addAll(
                makeTableColumn("Date",ATT_ALARM_DATE),
                makeTableColumn("Description",ATT_ALARM_DESCRIPTION),
                makeTableColumn("TreeElementsGroup",ATT_ALARM_GROUP),
                makeTableColumn("Acknowledge",ATT_ALARM_ACK)
        );
    }

    private TableColumn makeTableColumn(String header, final String ATTR_NAME){
        final TableColumn column = JavaFxUtil.value(ATTR_NAME, new TableColumn(header));
        column.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableCell<ClientAttributeWrapper, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        if (isEmpty) return;
                        updateColor();
                        changeResultTextFormat(item);
                    }
                    void updateColor(){
                        final TableRow tableRow = getTableRow();
                        if (null == tableRow) return;
                        final PresentationModel pm = (PresentationModel) tableRow.getItem();
                        if (null == pm) return;

                        final Boolean IS_ACKNOWLEDGE = (Boolean) pm.getAt(ATT_ALARM_ACK).getValue();

                        String cellColor = IS_ACKNOWLEDGE ?
                                "#000000" :
                                "#A60000";

                        //Set the CSS style on the cell and set the cell's text.
                        setStyle("-fx-text-fill:" + cellColor);
                    }
                    void changeResultTextFormat(Object item){
                        if (item != null) {
                            setText(item.toString().equals("true") ? "YES" :
                                    item.toString().equals("false") ? "NO" :
                                            item.toString());
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
        return column;
    }


    @FXML
    private void clearAlarmsButtonFired(ActionEvent actionEvent) {
        ClientStartupService.getInstance().getClientDolphin().send(Commands.ACKNOWLEDGE_ALARMS);
    }

    @FXML
    private void exitButtonFired(ActionEvent actionEvent) {
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
