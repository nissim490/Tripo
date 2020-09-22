package controller.algorithm;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import controller.Settings;
import controller.utils.Helper;
import controller.utils.Logger;
import controller.utils.TextAdapter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.NERCombinerAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import model.Sentiment;
import model.Text;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class SentimentBuilder {
    private final TextAdapter textAdapter;

    public SentimentBuilder() {
        textAdapter = TextAdapter.getInstance();
    }

    public Map<String, Sentiment> create(List<Text> texts) {
        Logger.info("SentimentBuilder creator process has been started!");
        Map<String, Sentiment> textSentiments = new ConcurrentHashMap<>();

        if ((new File(Settings.sentimentDirName + "/" + Settings.sentimentFileName)).isFile()) {
            Logger.info("SentimentBuilder reject creator process!");

            return this.load();
        }

        try {
            for (Text text : texts) {
                Sentiment sentiment = getTextSentiment(text);
                Helper.getInstance().writeSentimentDataToCsv(text, sentiment);
                textSentiments.put(text.getId(), sentiment);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        return textSentiments;
    }

    public Map<String, Sentiment> load() {
        Logger.info("SentimentBuilder loader process has been started!");
        Map<String, Sentiment> textSentiments = new ConcurrentHashMap<>();

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

    public Sentiment getTextSentiment(Text text) {

        Sentiment sentiment = new Sentiment();
        Set<String> words = textAdapter.textToWordSet(text.getContent());
        List<String> sentences = textAdapter.textToSentences(text.getContent());
        sentiment.setNumOfWords(words.size());
        sentiment.setNumOfSentences(sentences.size());

        for (String word : words) {

            int sentimentVal = findSentiment(word);
            switch (sentimentVal) {
                case 0:
                    sentiment.incVeryNegativeCountWords();
                    break;
                case 1:
                    sentiment.incNegativeCountWords();
                    break;
                case 2:
                    sentiment.incNaturalCountWords();
                    break;
                case 3:
                    sentiment.incPositiveCountWords();
                    break;
                case 4:
                    sentiment.incVeryPositiveCountWords();
                    break;
                default:
                    Logger.error("Invalid sentiment value " + sentimentVal + ":" + word);
            }
        }

        for (String sentence : sentences) {

            int sentimentVal = findSentiment(sentence);
            switch (sentimentVal) {
                case 0:
                    sentiment.incVeryNegativeCountSentences();
                    break;
                case 1:
                    sentiment.incNegativeCountSentences();
                    break;
                case 2:
                    sentiment.incNaturalCountSentences();
                    break;
                case 3:
                    sentiment.incPositiveCountSentences();
                    break;
                case 4:
                    sentiment.incVeryPositiveCountSentences();
                    break;
            }
        }

        return sentiment;
    }

    public int findSentiment(String line) {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, lemma,ner, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(line);

            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                System.out.println(sentence+"="+sentiment);

                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String wordSenti = token.get(SentimentCoreAnnotations.SentimentClass.class);
                    String tokensAndNERTags = token.ner();

                    System.out.println(token.word() + "\t" + wordSenti + " ner=" + tokensAndNERTags +"\t");
                }
            }


        }
        return 0;
    }
}
