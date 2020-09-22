package controller.ui;

import controller.utils.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import view.ViewStarter;

import java.io.IOException;

public class MainPageController {
    @FXML
    private Pane mainPane;

    @FXML
    private ImageView imgCover;

    @FXML
    private Button btnSettings;

    @FXML
    private Text txtResult;

    @FXML
    private Button btnDone1;

    @FXML
    void onBtnClassify(ActionEvent event) {
        Helper.getInstance().layoutSwitcher(mainPane, "pick_html_element.fxml","Load Site (1\\4)");
    }

    @FXML
    void onBtnSettings(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("./layouts/admin_panel.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

