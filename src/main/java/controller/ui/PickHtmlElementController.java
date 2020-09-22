package controller.ui;

import controller.utils.Helper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.Text;
import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PickHtmlElementController {
    @FXML
    private Pane mainPane;

    @FXML
    private WebView wvMain;

    @FXML
    private TextField txfUrlAdress;

    @FXML
    private Button btnLoad;

    @FXML
    private Button btnCapture;
    private WebEngine webEngine;

    @FXML
    void onCaptureBtnClick(ActionEvent event) {
        FXMLLoader fxmlLoader = Helper.getInstance().layoutSwitcher(mainPane, "html_element_confirmation.fxml", "Data Confirmation (2\\4)");
//        Document doc = webEngine.getDocument();
        Document doc = Jsoup.parseBodyFragment((String) webEngine.executeScript("document.body.outerHTML"));
        String textContent = doc.text().replaceAll("\\<.*?\\>|\\\\s+|&amp|&nbsp|\\[.*?\\]|\\{.*?\\}","");

        if (fxmlLoader != null) {
            ((ElementConfitmationController) fxmlLoader.getController()).updateUi(new Text(txfUrlAdress.getText(), textContent));
        }
    }


    /**
     * for communication to the Javascript engine.
     */
    JSObject javascriptConnector;

    /**
     * for communication from the Javascript engine.
     */
    JavaConnector javaConnector = new JavaConnector();

    public void initialize() {

    }

    @FXML
    void onLoadBtnClick(ActionEvent event) {
        Platform.runLater(new Runnable() {


            @Override
            public void run() {
                String urlAddresss = txfUrlAdress.getText();
                webEngine = wvMain.getEngine();
//                webEngine.setJavaScriptEnabled(true);
//
//                injectJsCode(webEngine);
//                Logger.info("JS Code have been inject!");
//
                webEngine.getLoadWorker().stateProperty()
                        .addListener((obs, oldValue, newValue) -> {
//                            Logger.info("WebEngine status is " + newValue);
                            txfUrlAdress.setText(webEngine.getLocation());
                        });

                webEngine.load(urlAddresss);
            }

            private void injectJsCode(WebEngine webEngine) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", javaConnector);

                String webJsInjection = Helper.getInstance().fileToString(getClass().getClassLoader().getResource("js/webJsInjection.js"));
                webEngine.executeScript(webJsInjection);

                // get the Javascript connector object.
                javascriptConnector = (JSObject) webEngine.executeScript("getJsConnector()");
            }
        });
    }

    public class JavaConnector {
        public JavaConnector() {

        }

        /**
         * called when the JS side wants a String to be converted.
         *
         * @param htmlRowDataElement the String to convert
         */
        public void captureHtml(String htmlRowDataElement) {

            if (null != htmlRowDataElement) {
                getTextObj(htmlRowDataElement);

                javascriptConnector.call("showResult", htmlRowDataElement);

            }
        }
    }

    private void getTextObj(String htmlRowDataElement) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                JSONParser jsonParser = new JSONParser();

                try {
                    JSONObject capture = (JSONObject) jsonParser.parse(htmlRowDataElement);

                    JSONArray attributesList = (JSONArray) capture.get("attributes");
                    String values = "";

                    for (Object attribute : attributesList) {
                        values += (((JSONObject) attribute).values() + " ");
                    }

                    values = values.replace("[", "").replace("]", "");

                    FXMLLoader fxmlLoader = Helper.getInstance().layoutSwitcher(mainPane, "html_element_confirmation.fxml", "Load Site");

                    if (fxmlLoader != null) {
                        ((ElementConfitmationController) fxmlLoader.getController()).updateUi(new Text(txfUrlAdress.getText(), capture.get("content").toString()));
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

