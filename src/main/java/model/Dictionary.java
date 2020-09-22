package model;

import controller.utils.TextTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dictionary {
    private final Set<String> dictionaryWords;
    private final Map<String, Map<String, Integer>> perExOccur;
    private final Map<String, Map<String, Integer>> promoOccur;
    private List<Integer> rejectionWordsIndexs = null;

    public Dictionary(Map<String, Map<String, Integer>> perExOccur, Map<String, Map<String, Integer>> promoOccur, Set<String> dictionaryWords) {
        this.perExOccur = perExOccur;
        this.promoOccur = promoOccur;
        this.dictionaryWords = dictionaryWords;
    }

    public Dictionary(Map<String, Map<String, Integer>> perExOccur, Map<String, Map<String, Integer>> promoOccur, Set<String> dictionaryWords, List<Integer> rejectionWordsIndexs) {
        this.perExOccur = perExOccur;
        this.promoOccur = promoOccur;
        this.dictionaryWords = dictionaryWords;
        this.rejectionWordsIndexs = rejectionWordsIndexs;
    }


    public Set<String> getDictionaryWords() {
        return dictionaryWords;
    }

    public Map<String, Map<String, Integer>> getPerExOccur() {
        return perExOccur;
    }

    public Map<String, Map<String, Integer>> getPromoOccur() {
        return promoOccur;
    }

    public ArrayList<Double> getOrderTextWordsOccur(Text text) {

        Map<String, Map<String, Integer>> occurList = null;

        if (text.getType() == TextTypeEnum.PersonalExperience)
            occurList = perExOccur;
        else
            occurList = promoOccur;

        if(occurList == null) return null;


        ArrayList<Double> listOfOccur = new ArrayList<>();

        for(String word : dictionaryWords){
            Map<String, Integer> wordMap = occurList.get(word);
            listOfOccur.add(wordMap.get(text.getId()).doubleValue());
        }

        return listOfOccur;
    }

    public List<Integer> getRejectionWordsIndexs() {
        return rejectionWordsIndexs;
    }
}
