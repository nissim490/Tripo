package controller.ui;

import controller.MongoDbController;
import controller.ObjectLoader;
import controller.ObjectsBuilder;
import controller.Settings;
import controller.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import model.Dictionary;
import model.Sentiment;
import model.Text;
import model.TextObject;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.normalize.InstanceNormalizeMidrange;
import view.ViewMainPageStarter;

import java.util.List;
import java.util.Map;

public class ObjectClassificationProcess {

    @FXML
    private Pane mainPane;

    @FXML
    private Button btnCancel;

    @FXML
    private ProgressBar progBar;

    @FXML
    private Line line1;

    @FXML
    private RadioButton rbTextCuptured;

    @FXML
    private RadioButton rbLoadDicWords;

    @FXML
    private RadioButton rbCalcWordsOccur;

    @FXML
    private RadioButton rbCreateDictionary;

    @FXML
    private RadioButton rbAnalizeSentiment;

    @FXML
    private RadioButton rbCreatingObject;

    @FXML
    private RadioButton rbNormailzeData;

    @FXML
    private RadioButton rbKnnClassification;

    @FXML
    private Line line2;

    @FXML
    private Line line3;

    @FXML
    private Line line7;

    @FXML
    private Line line6;

    @FXML
    private Line line5;

    @FXML
    private Line line4;

    @FXML
    private ProgressIndicator pbi1;

    @FXML
    private ProgressIndicator pbi2;

    @FXML
    private ProgressIndicator pbi3;

    @FXML
    private ProgressIndicator pbi4;

    @FXML
    private ProgressIndicator pbi5;

    @FXML
    private ProgressIndicator pbi6;

    @FXML
    private ProgressIndicator pbi7;

    @FXML
    private ProgressIndicator pbi8;

    private OnDoneEventListener processListener;

    class OnDoneEventListener {

        public void onDoneEvent(Object predictedClassValue) {
            Logger.debug("Performing callback after Asynchronous Task");

            FXMLLoader loader = Helper.getInstance().layoutSwitcher(mainPane, "classification_result.fxml", "Classification Result (4\\4)");
            if (predictedClassValue == null) {
                try {
                    throw new Exception("predictedClassValue is null");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ((ClassificationResultController) loader.getController()).updateUi(predictedClassValue);
            ViewMainPageStarter.primaryStage.setTitle("Finish: Classification Result (4\\4)");
        }
    }

    @FXML
    void onCancelBtnClick(ActionEvent event) {
        Helper.getInstance().layoutSwitcher(mainPane, "pick_html_element.fxml", "Load Site");
    }

    public void start(Text text) {
        ClassificationProcessThread classificationProcessThread = new ClassificationProcessThread(text);
        OnDoneEventListener mListener = new OnDoneEventListener();
        classificationProcessThread.registerOnDoneEventListener(mListener);
        classificationProcessThread.run();
    }


    public class ClassificationProcessThread {
        private OnDoneEventListener mListener;
        private Text text;
        private Object predictedClassValue;

        public ClassificationProcessThread(Text text) {
            this.text = text;
        }

        public void registerOnDoneEventListener(OnDoneEventListener mListener) {
            this.mListener = mListener;
        }


        // My Asynchronous task
        public void run() {
            // An Async task always executes in new thread
            new Thread(new Runnable() {
                public void run() {
                    updateUi(ClassificationProcessEnum.TextCuptured);
                    updateUi(ClassificationProcessEnum.LoadDicWords);

                    MongoDbController db = MongoDbController.getInstance();
                    ObjectLoader objectLoader = new ObjectLoader();
                    ObjectsBuilder objectsBuilder = new ObjectsBuilder();
                    Dictionary dictionary = db.getDictionary();
                    Map<String, Sentiment> sentiments = objectLoader.loadSentiment();
                    List<Text> texts = objectLoader.loadTexts();
                    dictionary = objectsBuilder.getFilteredDictionary(dictionary);


                    updateUi(ClassificationProcessEnum.CalcWordsOccur);

                    updateUi(ClassificationProcessEnum.CreateDictionary);

                    updateUi(ClassificationProcessEnum.AnalizeSentiment);

                    TextObject object = objectsBuilder.createInstance(text, dictionary);

                    updateUi(ClassificationProcessEnum.CreatingObject);

                    Instance instance = object.getMLInstance();

                    updateUi(ClassificationProcessEnum.NormailzeData);

                    Dataset data = new DefaultDataset();
                    List<TextObject> objects = objectsBuilder.createInstances(texts, dictionary, sentiments);

                    for (TextObject object2 : objects) {
                        data.add(object2.getMLInstance());
                    }

                    updateUi(ClassificationProcessEnum.KnnClassification);
                    KNearestNeighbors knn = new KNearestNeighbors(Settings.knnKvalue);
                    knn.buildClassifier(data);

                    InstanceNormalizeMidrange instanceNormalizeMidrange = new InstanceNormalizeMidrange();
                    instanceNormalizeMidrange.filter(data);
                    instanceNormalizeMidrange.filter(instance);

                    predictedClassValue = knn.classify(instance);
                    updateUi(ClassificationProcessEnum.Finish);
                }
            }).start();
        }

        public void updateUi(ClassificationProcessEnum state) {
            switch (state) {
                case TextCuptured:
                    progBar.setProgress(0.11);
                    progBar.getParent();
                    pbi1.setProgress(100);
                case LoadDicWords:
                    rbLoadDicWords.setDisable(false);
                    rbLoadDicWords.setSelected(true);
                    line1.setVisible(true);
                    progBar.setProgress(0.22);
                    pbi1.setProgress(100);
                    pbi2.setVisible(true);
                    break;
                case CalcWordsOccur:
                    rbCalcWordsOccur.setDisable(false);
                    rbCalcWordsOccur.setSelected(true);
                    line2.setVisible(true);
                    progBar.setProgress(0.33);
                    pbi2.setProgress(100);
                    pbi3.setVisible(true);
                    break;
                case CreateDictionary:
                    rbCreateDictionary.setDisable(false);
                    rbCreateDictionary.setSelected(true);
                    line3.setVisible(true);
                    progBar.setProgress(0.44);
                    pbi3.setProgress(100);
                    pbi4.setVisible(true);
                    break;
                case AnalizeSentiment:
                    rbAnalizeSentiment.setDisable(false);
                    rbAnalizeSentiment.setSelected(true);
                    line4.setVisible(true);
                    progBar.setProgress(0.55);
                    pbi4.setProgress(100);
//                blinkRunnable = new BlinkRunnable(rbAnalizeSentiment);
//                blinkRunnable.run();
                    pbi5.setVisible(true);
                    break;
                case CreatingObject:
//                blinkRunnable.shutdown();
                    rbCreatingObject.setDisable(false);
                    rbCreatingObject.setSelected(true);
                    line5.setVisible(true);
                    progBar.setProgress(0.66);
                    pbi5.setProgress(100);
                    pbi6.setVisible(true);
                    break;
                case NormailzeData:
                    rbNormailzeData.setDisable(false);
                    rbNormailzeData.setSelected(true);
                    line6.setVisible(true);
                    progBar.setProgress(0.77);
                    pbi6.setProgress(100);
                    pbi7.setVisible(true);
                    break;
                case KnnClassification:
                    line7.setVisible(true);
                    rbKnnClassification.setDisable(false);
                    rbKnnClassification.setSelected(true);
                    progBar.setProgress(0.88);
                    pbi7.setProgress(100);
                    pbi8.setVisible(true);
                    break;
                case Finish:
                    progBar.setProgress(1);

                    // check if listener is registered.
                    if (mListener != null) {
                        mListener.onDoneEvent(predictedClassValue);
                    }
                    break;
            }
        }
    }
}
