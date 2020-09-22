package controller;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import controller.utils.Logger;
import model.Dictionary;
import model.Sentiment;
import model.Text;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ObjectLoader {

    public ObjectLoader() {
    }

    public List<Text> loadTexts() {
        MongoDbController db = MongoDbController.getInstance();
        return db.getTextsAll();
    }

    public Dictionary loadDictionary() {
        Logger.info("DictionaryBuilder loader process has been started!");
        MongoDbController db = MongoDbController.getInstance();
        return db.getDictionary();
    }

    public Map<String, Sentiment> loadSentiment() {
        Logger.info("SentimentBuilder loader process has been started!");
        Map<String, Sentiment> textSentiments = new HashMap<>();

        try {
            Reader reader = Files.newBufferedReader(Paths.get(Settings.sentimentDirName + "/" + Settings.sentimentFileName));
            CSVReader csvReader = new CSVReader(reader);
            // Reading Records One by One in a String array
            String[] nextRecord;
            csvReader.readNext();
            while ((nextRecord = csvReader.readNext()) != null) {
                textSentiments.put(nextRecord[0], new Sentiment(
                        Integer.valueOf(nextRecord[2]),
                        Integer.valueOf(nextRecord[3]),
                        Integer.valueOf(nextRecord[4]),
                        Integer.valueOf(nextRecord[5]),
                        Integer.valueOf(nextRecord[6]),
                        Integer.valueOf(nextRecord[7]),
                        Integer.valueOf(nextRecord[8]),
                        Integer.valueOf(nextRecord[9]),
                        Integer.valueOf(nextRecord[10]),
                        Integer.valueOf(nextRecord[11]),
                        Integer.valueOf(nextRecord[12]),
                        Integer.valueOf(nextRecord[13])
                ));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return textSentiments;
    }


}
