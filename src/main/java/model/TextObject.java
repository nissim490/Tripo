package model;

import controller.Settings;
import controller.utils.Logger;
import controller.utils.TextTypeEnum;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import java.util.*;

public class TextObject {
    private List<Double> wordsOccur;
    private Sentiment sentiment;
    private TextTypeEnum type;
    private String _id;

    public TextObject(List<Double> wordsOccur, Sentiment sentiment, TextTypeEnum type) {
        this.wordsOccur = wordsOccur;
        this.sentiment = sentiment;
        this.type = type;
    }

    public TextObject(List<Double> wordsOccur, Sentiment sentiment) {
        this.wordsOccur = wordsOccur;
        this.sentiment = sentiment;
    }

    public TextObject(String _id, List<Double> wordsOccur, Sentiment sentiment) {
        this._id = _id;
        this.wordsOccur = wordsOccur;
        this.sentiment = sentiment;
    }

    public TextObject(Sentiment sentiment, TextTypeEnum type) {
        this.sentiment = sentiment;
        this.type = type;
    }

    public String get_id() {
        return _id;
    }

    public Instance getMLInstanceWithoutDic() {
        double[] featureList = new double[sentiment.getNumOfFields()];
        double numOfSentences = sentiment.getNumOfSentences();
        double numOfWords = sentiment.getNumOfWords();
        if (sentiment != null) {
            featureList[0] = ((sentiment.getNaturalCountSentences() / numOfSentences) * Settings.naturalCountSentencesWeight);
            featureList[1] = ((sentiment.getNegativeCountSentences() / numOfSentences) * Settings.negativeCountSentencesWeight);
            featureList[2] = ((sentiment.getPositiveCountSentences() / numOfSentences) * Settings.positiveCountSentencesWeight);
            featureList[3] = ((sentiment.getVeryNegativeCountSentences() / numOfSentences) * Settings.veryNegativeCountSentencesWeight);
            featureList[4] = ((sentiment.getVeryPositiveCountSentences() / numOfSentences) * Settings.veryPositiveCountSentencesWeight);
            featureList[5] = ((sentiment.getNaturalCountWords() / numOfWords) * Settings.naturalCountWordsWeight);
            featureList[6] = ((sentiment.getPositiveCountWords() / numOfWords) * Settings.positiveCountWordsWeight);
            featureList[7] = ((sentiment.getVeryNegativeCountWords() / numOfWords) * Settings.veryNegativeCountWordsWeight);
            featureList[8] = ((sentiment.getNegativeCountWords() / numOfWords) * Settings.negativeCountWordsWeight);
            featureList[9] = ((sentiment.getVeryPositiveCountWords() / numOfWords) * Settings.veryPositiveCountWordsWeight);
        }

        Instance instance = null;

        int numOfAttr = featureList.length;
        instance = new DenseInstance(numOfAttr);

        for (int i = 0; i < numOfAttr; i++) {

            instance.put(i, featureList[i]);

        }

        if (type != null) {
            instance.setClassValue(type);
        }

        Logger.info(sentiment.toString());

        return instance;
    }

    public Instance getMLInstance() {
        double[] featureList = new double[sentiment.getNumOfFields() + wordsOccur.size()];
        double numOfSentences = sentiment.getNumOfSentences();
        double numOfWords = sentiment.getNumOfWords();
        if (sentiment != null) {
            featureList[0] = ((sentiment.getNaturalCountSentences() / numOfSentences) * Settings.naturalCountSentencesWeight);
            featureList[1] = ((sentiment.getNegativeCountSentences() / numOfSentences) * Settings.negativeCountSentencesWeight);
            featureList[2] = ((sentiment.getPositiveCountSentences() / numOfSentences) * Settings.positiveCountSentencesWeight);
            featureList[3] = ((sentiment.getVeryNegativeCountSentences() / numOfSentences) * Settings.veryNegativeCountSentencesWeight);
            featureList[4] = ((sentiment.getVeryPositiveCountSentences() / numOfSentences) * Settings.veryPositiveCountSentencesWeight);
            featureList[5] = ((sentiment.getNaturalCountWords() / numOfWords) * Settings.naturalCountWordsWeight);
            featureList[6] = ((sentiment.getPositiveCountWords() / numOfWords) * Settings.positiveCountWordsWeight);
            featureList[7] = ((sentiment.getVeryNegativeCountWords() / numOfWords) * Settings.veryNegativeCountWordsWeight);
            featureList[8] = ((sentiment.getNegativeCountWords() / numOfWords) * Settings.negativeCountWordsWeight);
            featureList[9] = ((sentiment.getVeryPositiveCountWords() / numOfWords) * Settings.veryPositiveCountWordsWeight);
        }

        if (wordsOccur != null) {

            for (int i = 0; i < wordsOccur.size(); i++) {
                Double res = wordsOccur.get(i);
                if (res == null) {
                    featureList[i + sentiment.getNumOfFields()] = 0.0;
                } else {
                    featureList[i + sentiment.getNumOfFields()] = (res / numOfWords) * Settings.dictionaryWordWeight;
                }
            }
        }
        Instance instance = null;

        int numOfAttr = featureList.length;
        instance = new DenseInstance(numOfAttr);

        for (int i = 0; i < numOfAttr; i++) {
            instance.put(i, featureList[i]);
        }

        if (type != null) {
            instance.setClassValue(type);
        }

        Logger.info(sentiment.toString());

        return instance;
    }

    public Instance getMLInstance(List<Integer> rejectionWordsIndexs) {
        double[] featureList = new double[sentiment.getNumOfFields() + rejectionWordsIndexs.size()];
        double numOfSentences = sentiment.getNumOfSentences();
        double numOfWords = sentiment.getNumOfWords();
        if (sentiment != null) {
            featureList[0] = ((sentiment.getNaturalCountSentences() / numOfSentences) * Settings.naturalCountSentencesWeight);
            featureList[1] = ((sentiment.getNegativeCountSentences() / numOfSentences) * Settings.negativeCountSentencesWeight);
            featureList[2] = ((sentiment.getPositiveCountSentences() / numOfSentences) * Settings.positiveCountSentencesWeight);
            featureList[3] = ((sentiment.getVeryNegativeCountSentences() / numOfSentences) * Settings.veryNegativeCountSentencesWeight);
            featureList[4] = ((sentiment.getVeryPositiveCountSentences() / numOfSentences) * Settings.veryPositiveCountSentencesWeight);
            featureList[5] = ((sentiment.getNaturalCountWords() / numOfWords) * Settings.naturalCountWordsWeight);
            featureList[6] = ((sentiment.getPositiveCountWords() / numOfWords) * Settings.positiveCountWordsWeight);
            featureList[7] = ((sentiment.getVeryNegativeCountWords() / numOfWords) * Settings.veryNegativeCountWordsWeight);
            featureList[8] = ((sentiment.getNegativeCountWords() / numOfWords) * Settings.negativeCountWordsWeight);
            featureList[9] = ((sentiment.getVeryPositiveCountWords() / numOfWords) * Settings.veryPositiveCountWordsWeight);
        }

        if (wordsOccur != null) {
            int j = 0;
            for (int i = 0; i < wordsOccur.size(); i++) {
                if (rejectionWordsIndexs.contains(i)) {
                    Double res = wordsOccur.get(i);
//                    if (res == null) {
//                        featureList[j + sentiment.getNumOfFields()] = 0.0;
//                    } else {
                    featureList[j + sentiment.getNumOfFields()] = (res / numOfWords) * Settings.dictionaryWordWeight;
                    j++;
//                    }
                }
            }
        }
        Instance instance = null;

        int numOfAttr = featureList.length;
        instance = new DenseInstance(numOfAttr);

        for (int i = 0; i < numOfAttr; i++) {
            instance.put(i, featureList[i]);
        }

        if (type != null) {
            instance.setClassValue(type);
        }

        Logger.info(sentiment.toString());

        return instance;
    }

//    public Instance getMLInstanceWithoutSenti(int sizeOfDic) {
//        double[] featureList = new double[sizeOfDic];
//        double numOfSentences = sentiment.getNumOfSentences();
//        double numOfWords = sentiment.getNumOfWords();
//
//        if (wordsOccur != null) {
//            for (int i = 0; i < sizeOfDic; i++) {
//                Double res = wordsOccur.get(i);
//                if (res == null) {
//                    featureList[i] = 0.0;
//                } else {
//                    featureList[i] = (wordsOccur.get(i) / numOfWords) * Settings.dictionaryWordWeight;
//                }
//            }
//        }
//        Instance instance = null;
//
//        int numOfAttr = featureList.length;
//        instance = new DenseInstance(numOfAttr);
//
//        for (int i = 0; i < numOfAttr; i++) {
//            instance.put(i, featureList[i]);
//        }
//
//        if (type != null) {
//            instance.setClassValue(type);
//        }
//        return instance;
//    }


}


