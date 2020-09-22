package controller.ui;

import controller.Settings;
import controller.utils.Helper;
import controller.utils.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import model.Text;
import view.ViewMainPageStarter;

import java.io.IOException;

public class ElementConfitmationController {
    @FXML
    private Pane mainPane;

    @FXML
    private TextField txfUrlAdress;

    @FXML
    private Button btnInsetObject;

    @FXML
    private Button btnCancel;

    @FXML
    private TextArea txaContent;


    @FXML
    void onCancelBtnClick(ActionEvent event) {
        Helper.getInstance().layoutSwitcher(mainPane, "pick_html_element.fxml", "Load Site (1\\4)");
    }

    @FXML
    void onInsetObjectBtnClick(ActionEvent event) {
        String link = txfUrlAdress.getText();
        String content = txaContent.getText().replaceAll("\\n", "");

        Text text = new Text(link, content);
        Logger.warning(text.getJsonFormat());

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("./layouts/object_classification_process.fxml"));
            ObjectClassificationProcess objectClassificationProcess = new ObjectClassificationProcess();
            loader.setController(objectClassificationProcess);
            Parent newLoadedPane = loader.load();
            Scene scene = new Scene(newLoadedPane, 830, 880);
            ViewMainPageStarter.primaryStage.setScene(scene);
            ViewMainPageStarter.primaryStage.setTitle("Object Classification Process (3\\4)");
            ViewMainPageStarter.primaryStage.show();
            objectClassificationProcess.start(text);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    void txfHtmlAttr(ActionEvent event) {

    }

    @FXML
    void txfWebLink(ActionEvent event) {

    }


    public void updateUi(Text text) {
        txfUrlAdress.setText(text.getLink());

        String textContent = text.getContent();
        String textContentWithSpaces = "";
        int textContentSize = textContent.toCharArray().length;
        int prevIndex = 0;
        for (int i = 0; i < textContentSize; i++) {
            if (i != 0 && i % Settings.sizeOfLineInTextArea == 0) {
                textContentWithSpaces += (textContent.substring(prevIndex, i) + System.lineSeparator());
                prevIndex = i;
            }
        }

        txaContent.setText(textContentWithSpaces);
    }
}
