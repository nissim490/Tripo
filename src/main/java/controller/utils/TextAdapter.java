package controller.utils;

import model.DataItem;
import model.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TextAdapter {

    // static variable single_instance of type Singleton
    private static TextAdapter single_instance = null;

    // static method to create instance of Singleton class
    public static TextAdapter getInstance() {
        if (single_instance == null)
            single_instance = new TextAdapter();

        return single_instance;
    }

    // private constructor restricted to this class itself
    private TextAdapter() {

    }

    public List<Integer> getVectorByWord(Map<String, Map<String, Integer>> data, String word) {
        List<Integer> wordVec = new ArrayList<Integer>();

        for (Map<String, Integer> textMap : data.values()) {
            wordVec.add(textMap.get(word));
        }

        return wordVec;
    }

    public List<DataItem> getDataItemByWord(Map<String, Map<String, Integer>> data, String word) {
        List<DataItem> dataItem = new ArrayList<DataItem>();

        for (String key : data.keySet()) {
            dataItem.add(new DataItem(key, data.get(key).get(word)));
        }

        return dataItem;
    }


    public Map<String, Integer> calcOccur(Text text, Set<String> words) {
        Map<String, Integer> wordOccurText = new HashMap<>();

        String textContent = text.getContent().toLowerCase();
        for (String word : words) {
            int count = StringUtils.countMatches(textContent, word);
            wordOccurText.put(word, count);
        }

        return wordOccurText;
    }

    public Map<String, Map<String, Integer>> getOccur(List<Text> texts, Set<String> words) {
        Map<String, Map<String, Integer>> occurMap = new HashMap<>();

        for (Text text : texts) {
            Map<String, Integer> wordOccurText = calcOccur(text, words);
            occurMap.put(text.getId(), wordOccurText);
        }

        return occurMap;
    }


//    public Map<String, Map<String, Double>> getOccur(List<Text> texts, Set<String> words) {
//        Map<String, Map<String, Double>> occurList = new HashMap<>();
//
//        for (Text text : texts) {
//            Map<String, Double> wordOccurText = new HashMap<>();
//
//            for (String word : words) {
//                double count = StringUtils.countMatches(text.getContent().toLowerCase(), word);
//                wordOccurText.put(word, count);
//            }
//
//            text.setDictionary(wordOccurText);
//            occurList.put(text.getId(), wordOccurText);
//        }
//
//        return occurList;
//    }

    public List<Text> getTextsByType(List<Text> texts, TextTypeEnum type) {
        List<Text> tempList = new ArrayList<>();

        for (Text text : texts) {
            if (text.getType() == type) tempList.add(text);
        }

        return tempList;
    }

    public Set<String> getAllWordsFromTexts(List<Text> texts) {
        Set<String> words = new HashSet();

        for (Text text : texts) {
            words.addAll(textToWordSet(text.getContent()));
        }

        return words;
    }

    public Set<String> textToWordSet(String text) {
        return new HashSet<>(textToWordList(text));
    }

    public List<String> textToWordList(String text) {
        String[] words = text.split("\\W");

        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].toLowerCase();
        }

        return Arrays.asList(words);
    }

    public List<String> textToSentences(String text) {
        String[] sentences = text.split("\\n|\\.(?!\\d)|(?<!\\d)\\.");

        for (int i = 0; i < sentences.length; i++) {
            sentences[i] = sentences[i].toLowerCase();
        }

        return Arrays.asList(sentences);
    }
}
