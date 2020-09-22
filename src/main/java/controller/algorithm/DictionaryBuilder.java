package controller.algorithm;

import controller.Settings;
import controller.utils.Helper;
import controller.utils.Logger;
import controller.utils.TextAdapter;
import controller.utils.TextTypeEnum;
import model.Text;
import model.Dictionary;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.SpearmanRankCorrelation;

import java.util.*;

public class DictionaryBuilder {
    private TextAdapter textAdapter;
    private Dictionary dictionary;

    public DictionaryBuilder() {
        textAdapter = TextAdapter.getInstance();
    }

    public Dictionary init(List<Text> texts, List<Text> perExTexts, List<Text> promoTexts, Set<String> dictionaryWords) {
        Logger.info("DictionaryBuilder initialization process has been started!");
//        List<String> dictionaryWords = new ArrayList<>(textAdapter.getAllWordsFromTexts(texts));
//        dictionaryWords.removeAll(db.getStopWords());
//        Map<String, Map<String, Integer>> occurMap = textAdapter.getOccur(texts, dictionaryWords);

        Map<String, Map<String, Integer>> perExOccur = textAdapter.getOccur(perExTexts, dictionaryWords);
        Map<String, Map<String, Integer>> promoOccur = textAdapter.getOccur(promoTexts, dictionaryWords);
        Logger.info("DictionaryBuilder: occurrence vectors is ready!");

        /*
         * The simplest incarnation of the DenseInstance constructor will only
         * take a double array as argument an will create an instance with given
         * values as attributes and no class value set. For unsupervised machine
         * learning techniques this is probably the most convenient constructor.
         */
        SpearmanRankCorrelation spearmanRankCorr = new SpearmanRankCorrelation();

        Map<String, Double> destMap = new HashMap<>();
        for (String word : dictionaryWords) {

            List<Integer> perExVecWord = textAdapter.getVectorByWord(perExOccur, word);
            Instance perExInstance = new DenseInstance(perExVecWord.stream().mapToDouble(d -> d).toArray(), TextTypeEnum.PersonalExperience.toString());

            List<Integer> promoVecWord = textAdapter.getVectorByWord(promoOccur, word);
            Instance promoInstance = new DenseInstance(promoVecWord.stream().mapToDouble(d -> d).toArray(), TextTypeEnum.Promotion.toString());

            double dest = spearmanRankCorr.measure(perExInstance, promoInstance);
            destMap.put(word, dest);
            Helper.getInstance().writeDictionaryWordVectorToCsv(word, perExVecWord, promoVecWord);
        }

        if (Settings.avgSpearmanRankCorrelationThreshold > 100 || Settings.avgSpearmanRankCorrelationThreshold < 0) {
            throw new IllegalArgumentException("" +
                    "avgSpearmanRankCorrelationThreshold should be betweenvalue must be 0 < x < 100 given: "
                    + Settings.avgSpearmanRankCorrelationThreshold
            );

        }

        List<Double> destList = new ArrayList<>(destMap.values());
        int destListSize = destList.size();
        int sizeOfUnit = Math.toIntExact(Math.round(destListSize / Settings.avgSpearmanRankCorrelationThreshold));
        Logger.debug("size of spearman unit: " + destListSize + "/" + Settings.avgSpearmanRankCorrelationThreshold + " ~ " +sizeOfUnit);

        Collections.sort(destList);
        List<Double> keysToRemove = new ArrayList<>();

        for (int i = 0; i < sizeOfUnit; i++) {
            keysToRemove.add(destList.get(i));
        }

        boolean isWordToRemove;
        for (String key : destMap.keySet()) {
            isWordToRemove = false;
            if (keysToRemove.contains(destMap.get(key))) {
                dictionaryWords.remove(key);
                isWordToRemove = true;
            }
            Helper.getInstance().writeDictionaryDataToCsv(key, destMap.get(key), isWordToRemove);
        }

        Logger.info("Dictionary is ready to use!");
        Helper.getInstance().writeDictionaryWordsCsv(dictionaryWords);
        return new Dictionary(perExOccur, promoOccur, dictionaryWords);
    }

    public Dictionary getDictionary() {
        return dictionary;
    }
}
