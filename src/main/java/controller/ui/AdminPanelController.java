package controller.ui;

import controller.MongoDbController;
import controller.ObjectLoader;
import controller.ObjectsBuilder;
import controller.Settings;
import controller.utils.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import model.*;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.javaml.filter.normalize.InstanceNormalizeMidrange;
import org.apache.commons.io.*;

public class AdminPanelController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TextArea txtAreaLogger;

    @FXML
    private Spinner<Double> spVNegWords;

    @FXML
    private Spinner<Double> spNegWords;

    @FXML
    private Spinner<Double> spNaturalWords;

    @FXML
    private Spinner<Double> spPosWords;

    @FXML
    private Spinner<Double> spVPosWords;

    @FXML
    private Spinner<Double> spVNegSen;

    @FXML
    private Spinner<Double> spNegSen;

    @FXML
    private Spinner<Double> spNatSen;

    @FXML
    private Spinner<Double> spPosSen;

    @FXML
    private Spinner<Double> spVPosSen;

    @FXML
    private Spinner<Integer> spMinSizeOfWord;

    @FXML
    private Spinner<Integer> spWordOccurTH;

    @FXML
    private Spinner<Double> spSpermanCRTH;


    public void initialize() {
        initSpinnersValues();
    }

    private void writeToLocalLogger(String line) {
        String text = txtAreaLogger.getText();
        txtAreaLogger.setText(text + "\n" + line);
        Logger.info(line);
    }

    public void initSpinnersValues() {
        spVNegWords.getValueFactory().setValue(Settings.veryNegativeCountWordsWeight);
        spNegWords.getValueFactory().setValue(Settings.negativeCountWordsWeight);
        spNaturalWords.getValueFactory().setValue(Settings.naturalCountWordsWeight);
        spPosWords.getValueFactory().setValue(Settings.positiveCountWordsWeight);
        spVPosWords.getValueFactory().setValue(Settings.veryPositiveCountWordsWeight);
        spVNegSen.getValueFactory().setValue(Settings.veryNegativeCountSentencesWeight);
        spNegSen.getValueFactory().setValue(Settings.negativeCountSentencesWeight);
        spNatSen.getValueFactory().setValue(Settings.naturalCountSentencesWeight);
        spPosSen.getValueFactory().setValue(Settings.positiveCountSentencesWeight);
        spVPosSen.getValueFactory().setValue(Settings.veryPositiveCountSentencesWeight);
        spMinSizeOfWord.getValueFactory().setValue(Settings.minSizeOfWord);
        spWordOccurTH.getValueFactory().setValue(Settings.wordOccurTH);
        spSpermanCRTH.getValueFactory().setValue(Settings.spearmanRankCorrelationThreshold);

        spVNegWords.setEditable(true);
        spNegWords.setEditable(true);
        spNaturalWords.setEditable(true);
        spPosWords.setEditable(true);
        spVPosWords.setEditable(true);
        spVNegSen.setEditable(true);
        spNegSen.setEditable(true);
        spNatSen.setEditable(true);
        spPosSen.setEditable(true);
        spVPosSen.setEditable(true);
        spMinSizeOfWord.setEditable(true);
        spWordOccurTH.setEditable(true);
        spSpermanCRTH.setEditable(true);

        spVNegWords.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.veryNegativeCountWordsWeight = new Double(newValue);
            writeToLocalLogger("veryNegativeCountWordsWeight value set to: " + newValue);
        });
        spNegWords.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.negativeCountWordsWeight = new Double(newValue);
            writeToLocalLogger("negativeCountWordsWeight value set to: " + newValue);
        });
        spNaturalWords.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.naturalCountWordsWeight = new Double(newValue);
            writeToLocalLogger("naturalCountWordsWeight value set to: " + newValue);
        });
        spPosWords.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.positiveCountWordsWeight = new Double(newValue);
            writeToLocalLogger("positiveCountWordsWeight value set to: " + newValue);
        });
        spVPosWords.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.veryPositiveCountWordsWeight = new Double(newValue);
            writeToLocalLogger("veryPositiveCountWordsWeight value set to: " + newValue);
        });
        spVNegSen.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.veryNegativeCountSentencesWeight = new Double(newValue);
            writeToLocalLogger("veryNegativeCountSentencesWeight value set to: " + newValue);
        });
        spNegSen.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.negativeCountSentencesWeight = new Double(newValue);
            writeToLocalLogger("negativeCountSentencesWeight value set to: " + newValue);
        });
        spNatSen.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.naturalCountSentencesWeight = new Double(newValue);
            writeToLocalLogger("naturalCountSentencesWeight value set to: " + newValue);
        });
        spPosSen.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.positiveCountSentencesWeight = new Double(newValue);
            writeToLocalLogger("positiveCountSentencesWeight value set to: " + newValue);
        });
        spVPosSen.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.veryPositiveCountSentencesWeight = new Double(newValue);
            writeToLocalLogger("veryPositiveCountSentencesWeight value set to: " + newValue);
        });
        spMinSizeOfWord.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.minSizeOfWord = new Integer(newValue);
            writeToLocalLogger("minSizeOfWord value set to: " + newValue);
        });
        spWordOccurTH.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.wordOccurTH = new Integer(newValue);
            writeToLocalLogger("wordOccurTH value set to: " + newValue);
        });
        spSpermanCRTH.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.spearmanRankCorrelationThreshold = new Double(newValue);
            writeToLocalLogger("spearmanRankCorrelationThreshold value set to: " + newValue);
        });
    }

    @FXML
    void createTrainingSet(ActionEvent event) {
        createDirectories();
        Dataset data = new DefaultDataset();
        MongoDbController db = MongoDbController.getInstance();
        List<Text> texts = db.getTextsAll();
        TextAdapter textAdapter = TextAdapter.getInstance();
        List<Text> perExTexts = textAdapter.getTextsByType(texts, TextTypeEnum.PersonalExperience);
        List<Text> promoTexts = textAdapter.getTextsByType(texts, TextTypeEnum.Promotion);

        List<TextObject> objects = new ObjectsBuilder().build(texts, perExTexts, promoTexts);
//        data.addAll(objects);
//        Helper.getInstance().writeDataToCsv(data);
    }

    @FXML
    void createTrainingSetSentimentOnly(ActionEvent event) {
    }

    @FXML
    void createTrainingSetDictionaryOnly(ActionEvent event) {

    }

    @FXML
    Dataset loadTrainingSet(ActionEvent event) {
        Dataset data = null;
        try {
            data = Helper.getInstance().loadDataFromCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @FXML
    void deleteTrainingSet(ActionEvent event) {
        (new File(Settings.outputDirName + "/" + Settings.datasetFileName)).delete();
    }

    @FXML
    void loadDataFromSites(ActionEvent event) {
        RowDataHelper.getInstance().writeTextsFromCsv("./csv/texts_urls_by_types_550.csv");
    }

    @FXML
    void deleteTempFiles(ActionEvent event) {
        try {
            FileUtils.deleteDirectory(new File(Settings.dictionaryWordDirName));
            FileUtils.deleteDirectory(new File(Settings.sentimentDirName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createDirectories() {
        List<File> dirs = new ArrayList<>();

        dirs.add(new File(Settings.outputDirName));
        dirs.add(new File(Settings.sentimentDirName));
        dirs.add(new File(Settings.dictionaryDirName));
        dirs.add(new File(Settings.dictionaryWordDirName));

        for (File file : dirs) {
            if (!file.isDirectory()) {
                Logger.debug("Directory created: " + file.getPath());
                file.mkdirs();
            }
        }
    }

    @FXML
    private void createObjectsForTest() {
        new Runnable() {
            @Override
            public void run() {
                writeToLocalLogger("Create Objects For Test...");
                MongoDbController db = MongoDbController.getInstance();
                Dictionary dictionary = db.getDictionary();

                ObjectsBuilder objectsBuilder = new ObjectsBuilder();

                List<Text> personalExperienceTexts = db.getTestTexts(TextTypeEnum.PersonalExperience);
                List<Text> promotionTexts = db.getTestTexts(TextTypeEnum.Promotion);

                for (Text text : personalExperienceTexts) {
                    TextObject object = objectsBuilder.createInstance(text, dictionary);
                }

                for (Text text : promotionTexts) {
                    TextObject object = objectsBuilder.createInstance(text, dictionary);
                }
            }
        }.run();
    }

    @FXML
    void onBack(ActionEvent event) {
        Helper.getInstance().layoutSwitcher(mainPane, "main_page.fxml", "Load Site");
    }

    @FXML
    private void runSuccessTest() {
        new Runnable() {
            @Override
            public void run() {

                MongoDbController db = MongoDbController.getInstance();
                ObjectLoader objectLoader = new ObjectLoader();
                Dictionary dictionary = objectLoader.loadDictionary();

                Map<String, Sentiment> sentiments = objectLoader.loadSentiment();
                List<Text> texts = objectLoader.loadTexts();

                ObjectsBuilder objectsBuilder = new ObjectsBuilder();
                dictionary = objectsBuilder.getFilteredDictionary(dictionary);
                List<TextObject> objects = objectsBuilder.createInstances(texts, dictionary, sentiments);
                int sizeOfDic = dictionary.getDictionaryWords().size();
                writeToLocalLogger("Size of dictionary: " + sizeOfDic);

                Dataset data = new DefaultDataset();

                for (TextObject object : objects) {
                    data.add(object.getMLInstance());
                }
                InstanceNormalizeMidrange instanceNormalizeMidrange = new InstanceNormalizeMidrange();
                instanceNormalizeMidrange.filter(data);

                KNearestNeighbors knn = new KNearestNeighbors(Settings.knnKvalue);
                knn.buildClassifier(data);

                int successCounter = 0;

                List<TextObject> objectsForTest = db.getTestObjects();
                Map<String, String> objectsForTestTypes = db.getTestObjectsTypes();

                for (TextObject object : objectsForTest) {
                    Instance instance = object.getMLInstance(dictionary.getRejectionWordsIndexs());
                    instanceNormalizeMidrange.filter(instance);
                    Object predictedClassValue = knn.classify(instance);
                    writeToLocalLogger(object.get_id() +" (" +objectsForTestTypes.get(object.get_id()) + ") ?=" + predictedClassValue);
                    String res = objectsForTestTypes.get(object.get_id());
                    if (res != null && res.equals(predictedClassValue.toString())) {
                        successCounter++;
                    }
                }
                String msg = "Success Average: " + ((float) ((successCounter / (float) objectsForTest.size()) * 100)) + "%";

                writeToLocalLogger(msg);
                writeToLocalLogger("|--------------------------------End-Of-Task--------------------------------|");
            }
        }.run();
    }
}

