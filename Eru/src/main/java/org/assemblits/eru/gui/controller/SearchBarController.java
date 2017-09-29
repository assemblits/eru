package org.assemblits.eru.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Created by mtrujillo on 9/29/17.
 */
@Component
@Data
public class SearchBarController {
    @FXML
    private TextField searchTextField;
}
