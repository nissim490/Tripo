package controller.ui;

import controller.utils.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import view.ViewMainPageStarter;

public class ClassificationResultController {

    @FXML
    private Pane mainPane;

    @FXML
    private Button btnDone;

    @FXML
    private Text txtResult;

    @FXML
    void onBtnDone(ActionEvent event) {
        Helper.getInstance().layoutSwitcher(mainPane, "pick_html_element.fxml", "Classification Result");
    }

    public void updateUi(Object predictedClassValue) {
        txtResult.setText(predictedClassValue.toString());
    }
}

