package controller.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import controller.Settings;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import model.Sentiment;
import model.Text;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;
import view.ViewMainPageStarter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Helper {
    // static variable single_instance of type Singleton
    private static Helper single_instance = null;

    // static method to create instance of Singleton class
    public static Helper getInstance() {
        if (single_instance == null)
            single_instance = new Helper();

        return single_instance;
    }

    // private constructor restricted to this class itself
    private Helper() {

    }

    public String fileToString(URL filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath.getPath()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    public void writeDataToCsv(Dataset data) {
        try {
            FileHandler.exportDataset(data, new File(Settings.outputDirName + "/" + Settings.datasetFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FXMLLoader layoutSwitcher(Pane parent, String layout, String title) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent newLoadedPane = fxmlLoader.load(getClass().getClassLoader().getResource("./layouts/" + layout).openStream());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    parent.getChildren().setAll(newLoadedPane);
                }
            });
            try {
                ViewMainPageStarter.primaryStage.setTitle(title);
            } catch (Exception ex) {
//                ex.printStackTrace();
            }
            return fxmlLoader;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double calculateAverage(Collection<Double> marks) {
        Double sum = 0.0;
        if (!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    public Dataset loadDataFromCsv() throws IOException {
        return FileHandler.loadDataset(new File(Settings.outputDirName + "/" + Settings.datasetFileName));
    }

    public void writeDictionaryDataToCsv(String word, Double spearmanDistance, boolean isWordToRemove) {

        try {
            File file = new File(Settings.dictionaryDirName + "/" + Settings.dictionarySpearmanDistanceFileName);

            if (file.createNewFile()) {
                CSVWriter writer = new CSVWriter(new FileWriter(file, true));
                System.out.println("File created: " + file.getName());
                String[] record = new String[]{"word", "spearmanDistance", "isWordToRemove"};
                writer.writeNext(record);
                writer.close();
            }

            CSVWriter writer = new CSVWriter(new FileWriter(file, true));
            String[] record = new String[]{word, spearmanDistance.toString(), String.valueOf(isWordToRemove)};
            writer.writeNext(record);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeSentimentDataToCsv(Text text, Sentiment sentiment) {
        writeSentimentDataToCsv(text, sentiment, Settings.sentimentFileName);
    }

    public void writeSentimentDataToCsv(Text text, Sentiment sentiment, String csvFileName) {
        try {
            File file = new File(Settings.sentimentPath + "/" + csvFileName);

            if (file.createNewFile()) {
                CSVWriter writer = new CSVWriter(new FileWriter(file, true));
                Logger.debug("File created: " + file.getName());

                String[] record = new String[]{
                        "textId",
                        "type",
                        "veryNegativeCountWords",
                        "negativeCountWords",
                        "naturalCountWords",
                        "positiveCountWords",
                        "veryPositiveCountWords",
                        "veryNegativeCountSentences",
                        "negativeCountSentences",
                        "naturalCountSentences",
                        "positiveCountSentences",
                        "veryPositiveCountSentences",
                        "numOfWords",
                        "numOfSentences"};


                writer.writeNext(record);
                writer.close();
            }

            CSVWriter writer = new CSVWriter(new FileWriter(file, true));
            String[] record = new String[]{
                    text.getId(),
                    text.getType().toString(),
                    String.valueOf(sentiment.getVeryNegativeCountWords()),
                    String.valueOf(sentiment.getNegativeCountWords()),
                    String.valueOf(sentiment.getNaturalCountWords()),
                    String.valueOf(sentiment.getPositiveCountWords()),
                    String.valueOf(sentiment.getVeryPositiveCountWords()),
                    String.valueOf(sentiment.getVeryNegativeCountSentences()),
                    String.valueOf(sentiment.getNegativeCountSentences()),
                    String.valueOf(sentiment.getNaturalCountSentences()),
                    String.valueOf(sentiment.getPositiveCountSentences()),
                    String.valueOf(sentiment.getVeryPositiveCountSentences()),
                    String.valueOf(sentiment.getNumOfWords()),
                    String.valueOf(sentiment.getNumOfSentences()),};
            writer.writeNext(record);
            writer.close();
            Logger.debug("Sentiment for text '" + text.getId() + "' added!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDictionaryWordVectorToCsv(String word, List<Integer> perExOccur, List<Integer> promoOccur) {
        try {
            File file = new File(Settings.dictionaryWordDirName + "/" + word + ".csv");

            if (file.createNewFile()) {
                CSVWriter writer = new CSVWriter(new FileWriter(file, true));
                Logger.debug("File created: " + file.getName());

                String[] record = new String[]{
                        "word",
                        "perExOccur",
                        "promoOccur"};


                writer.writeNext(record);
                writer.close();
            }

            CSVWriter writer = new CSVWriter(new FileWriter(file, true));

            for (int i = 0; i < perExOccur.size(); i++) {
                String[] record = new String[]{
                        word,
                        String.valueOf(perExOccur.get(i)),
                        String.valueOf(promoOccur.get(i))};
                writer.writeNext(record);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDictionaryWordsCsv(Set<String> dictionaryWords) {
        try {
            File file = new File(Settings.dictionaryDirName + "/" + Settings.dictionaryWordsFileName);

            if (file.createNewFile()) {
                CSVWriter writer = new CSVWriter(new FileWriter(file, true));
                Logger.debug("File created: " + file.getName());

                for (String word : dictionaryWords) {
                    String[] record = new String[]{word};
                    writer.writeNext(record);
                }

                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> loadDictionaryWordsCsv() {
        Set<String> list = new HashSet<>();
        String[] word = new String[1];

        try {
            File file = new File(Settings.dictionaryDirName + "/" + Settings.dictionaryWordsFileName);
            FileReader reader = new FileReader(file);
            CSVReader csvReader = new CSVReader(reader);

            while ((word = csvReader.readNext()) != null) {
                list.add(word[0]);
            }

            reader.close();
            csvReader.close();

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void writeRejectWordsCSV(String word, String rejectionCause) {
        try {
            File file = new File(Settings.dictionaryDirName + "/" + Settings.wordsRejectionFileName);

            if (file.createNewFile()) {
                CSVWriter writer = new CSVWriter(new FileWriter(file, true));
                System.out.println("File created: " + file.getName());
                String[] record = new String[]{"word", "cause"};
                writer.writeNext(record);
                writer.close();
            }

            CSVWriter writer = new CSVWriter(new FileWriter(file, true));
            String[] record = new String[]{word, rejectionCause};
            writer.writeNext(record);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
