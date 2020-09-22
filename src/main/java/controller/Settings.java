package controller;

import java.io.File;

public class Settings {
    //# CSV files
    public static final String csvWebSitesFilePath = "./csv/texts_urls_by_type.csv";
    public static final String dictionarySpearmanDistanceFileName = "dictionarySpearmanDistance.csv";
    public static final String sentimentFileName = "sentiment.csv";

    //# JS files
    public static final String jsInjectionCaptureByUserFilePath = "./csv/webJsInjection.csv";
    public static final String jsInjectionAutoCaptureFilePath = "./csv/texts_urls_by_type_for_test.csv";

    //# Logs
    public static final String siteFailLoadLogFileName = "site_fail_load.log";

    //# Dataset
    public static final String datasetFileName = "dataset.csv";

    //# Algorithms
    public static double avgSpearmanRankCorrelationThreshold = 20;
    public static int knnKvalue = 3;

    //# Text Object Weight
    public static double dictionaryObjWeight = 1.0;
    public static double veryNegativeCountWordsWeight = 0.0;
    public static double negativeCountWordsWeight = 0.0;
    public static double naturalCountWordsWeight = 0.0;
    public static double positiveCountWordsWeight = 0.0;
    public static double veryPositiveCountWordsWeight = 0.0;
    public static double veryNegativeCountSentencesWeight = 0.0;
    public static double negativeCountSentencesWeight = 0.0;
    public static double naturalCountSentencesWeight = 0.0;
    public static double positiveCountSentencesWeight = 0.0;
    public static double veryPositiveCountSentencesWeight = 0.0;

    public static double dictionaryWordWeight = 1;

    //#Directories
    public static String sentimentPath = "./output/sentiment/";
    public static String outputDirName = "output";
    public static String sentimentDirName = outputDirName + "/sentiment";
    public static String dictionaryDirName = outputDirName + "/dictionary";
    public static String dictionaryWordDirName = dictionaryDirName + "/words";;
    public static int sizeOfLineInTextArea = 135;
    public static String dictionaryWordsFileName = "dictionaryWords.csv";
    public static int minSizeOfWord = 1;
    public static String wordsRejectionFileName = "wordsRejectionList.csv";
    public static int wordOccurTH = 1;
    public static double spearmanRankCorrelationThreshold = 0.9999999;
}
