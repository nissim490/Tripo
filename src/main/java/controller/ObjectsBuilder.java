package controller;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellChecker;
import controller.algorithm.DictionaryBuilder;
import controller.utils.Helper;
import controller.utils.Logger;
import controller.utils.TextAdapter;
import controller.utils.TextTypeEnum;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import model.*;
import model.Dictionary;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.SpearmanRankCorrelation;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectsBuilder {
    private Map<String, String> rejectionWordsMap;

    public ObjectsBuilder() {
        rejectionWordsMap = new HashMap<>();
    }

    public List<TextObject> build(List<Text> texts, List<Text> perExTexts, List<Text> promoTexts) {
        Logger.info("ObjectsBuilder process has been started!");

        String dictFile = "./bank/english.0";
        String englishPhonetic = "./bank/phonet.en";
        SpellChecker spellCheck = null;

        SpellDictionary spellDictionary = null;

        try {
            spellDictionary = new SpellDictionaryHashMap(new File(getClass().getClassLoader().getResource(dictFile).getFile()), new File(getClass().getClassLoader().getResource(englishPhonetic).getFile()));
            spellCheck = new SpellChecker(spellDictionary);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> dictionaryWords = new HashSet<>();

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, lemma, ner, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Map<String, Sentiment> textSentiments = new HashMap<>();

        for (Text text : texts) {
            Annotation annotation = pipeline.process(text.getContent());
            Sentiment sentiment = new Sentiment();

            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                updateSentenceSentiment(sentiment, sentence);

                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String word = updateWordSentiment(sentiment, token, spellCheck);

                    if (word != null) {
                        dictionaryWords.add(word);
                        System.out.println(word);
                    } else {
                        System.out.println("null");
                    }


                }
                System.out.println(sentence);
            }

            Helper.getInstance().writeSentimentDataToCsv(text, sentiment);
            textSentiments.put(text.getId(), sentiment);
        }

        for (String word : rejectionWordsMap.keySet()) {
            Helper.getInstance().writeRejectWordsCSV(word, rejectionWordsMap.get(word));
        }

        DictionaryBuilder dictionaryBuilder = new DictionaryBuilder();
        Dictionary dictionary = dictionaryBuilder.init(texts, perExTexts, promoTexts, dictionaryWords);

        MongoDbController db = MongoDbController.getInstance();
        db.addDictionaryWords(dictionaryWords);
        db.addDictionary(dictionary);

        List<TextObject> objects = createInstances(texts, dictionary, textSentiments);

        return objects;
    }

    public List<TextObject> getObjects() {
        MongoDbController db = MongoDbController.getInstance();
        List<Text> texts = db.getTextsAll();
        Dictionary dictionary = db.getDictionary();
        Map<String, Sentiment> textSentiments = db.getTextSentiments();

        List<TextObject> objects = createInstances(texts, dictionary, textSentiments);
        return objects;
    }

    public void updateSentenceSentiment(Sentiment sentiment, CoreMap sentence) {
        sentiment.incNumOfSentences();
        Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        int sentenceSenti = RNNCoreAnnotations.getPredictedClass(tree);

        switch (sentenceSenti) {
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
            default:
                Logger.error("Invalid sentiment value " + sentenceSenti + ":" + sentence);
        }
    }

    public String updateWordSentiment(Sentiment sentiment, CoreLabel token, SpellChecker spellCheck) {
        sentiment.incNumOfWords();

        String wordSenti = token.get(SentimentCoreAnnotations.SentimentClass.class);
        if (wordSenti != null) {
            switch (wordSenti) {
                case "Very negative":
                    sentiment.incVeryNegativeCountWords();
                    break;
                case "Negative":
                    sentiment.incNegativeCountWords();
                    break;
                case "Neutral":
                    sentiment.incNaturalCountWords();
                    break;
                case "Positive":
                    sentiment.incPositiveCountWords();
                    break;
                case "Very positive":
                    sentiment.incVeryPositiveCountWords();
                    break;
                default:
                    Logger.error("Invalid sentiment value " + wordSenti + ":" + token.word());
            }
        }

        return filer(token, spellCheck);
    }

    private String filer(CoreLabel token, SpellChecker spellCheck) {
        //toLowerCase
        String word = token.value().toLowerCase();

        //check if contain other chars
        String validPattern = "[^a-zA-Z]";
        Pattern pattern = Pattern.compile(validPattern);
        Matcher matcher = pattern.matcher(word);

        if (matcher.find()) {
            rejectionWordsMap.put(word, "IncludeNonLatterCharacter");
            return null;
        }

//        List<String> splitChars = new ArrayList<>();
//        splitChars.add("-");
//        splitChars.add("/");
//
//        if (matcher.find()) {
//            boolean isJoinedWord = false;
//
//            for (String spChar : splitChars) {
//                if (word.contains(spChar)) {
//                    isJoinedWord = true;
//                    String subWordStart = word.substring(0, word.indexOf(spChar));
//                    {
//                        Matcher matcherTemp = pattern.matcher(subWordStart);
//                        if (matcherTemp.find()) {
//                            rejectionWordsMap.put(word, "IncludeNonLatterCharacter");
//                            return null;
//                        }
//                    }
//                    String subWordEnd = word.substring(word.indexOf(spChar, word.length()));
//                    {
//                        Matcher matcherTemp = pattern.matcher(subWordEnd);
//                        if (matcherTemp.find()) {
//                            rejectionWordsMap.put(word, "IncludeNonLatterCharacter");
//                            return null;
//                        }
//                    }
//                }
//            }
//
//            if (!isJoinedWord) {
//                rejectionWordsMap.put(word, "IncludeNonLatterCharacter");
//                return null;
//            }
//        }

        //NER Check
        List<String> excludeNer = new ArrayList<String>(Arrays.asList("DATE", "NUMBER"));
        if (excludeNer.contains(token.ner())) {
            rejectionWordsMap.put(word, token.ner());
            return null;
        }

        try {
            List suggestions = spellCheck.getSuggestions(word, 100);
            if (suggestions.isEmpty()) {
                rejectionWordsMap.put(word, "SpellCheck");
                return null;
            }
        } catch (Exception ex) {
            rejectionWordsMap.put(word, "NegSpellCheck");
            return null;
        }

        //word stemmer
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        word = stemmer.getCurrent();

        //check word size
        if (word.length() <= Settings.minSizeOfWord) {
            rejectionWordsMap.put(word, "NumberOfChars");
            return null;
        }

        return word;
    }

    public TextObject createInstance(Text text, Dictionary dictionary) {
        SpellChecker spellCheck = null;
        String dictFile = "./bank/english.0";
        String englishPhonetic = "./bank/phonet.en";
        Set<String> dictionaryWords = dictionary.getDictionaryWords();
        SpellDictionary spellDictionary = null;

        try {
            spellDictionary = new SpellDictionaryHashMap(new File(getClass().getClassLoader().getResource(dictFile).getFile()), new File(getClass().getClassLoader().getResource(englishPhonetic).getFile()));
            spellCheck = new SpellChecker(spellDictionary);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, lemma, ner, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Set<String> words = new HashSet<>();

        Annotation annotation = pipeline.process(text.getContent());
        Sentiment sentiment = new Sentiment();

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            updateSentenceSentiment(sentiment, sentence);

            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = updateWordSentiment(sentiment, token, spellCheck);

                if (word != null && dictionaryWords.contains(word)) {
                    words.add(word);
                    Logger.info(word);
                }
            }

        }

        TextAdapter textAdapter = TextAdapter.getInstance();
        Map<String, Integer> textOucrr = textAdapter.calcOccur(text, words);
        List<Double> orderTextOucrr = new ArrayList<>();

        for (String word : dictionaryWords) {
            Integer res = (textOucrr.get(word));
            if (res != null) {
                orderTextOucrr.add((textOucrr.get(word)).doubleValue());
            } else {
                orderTextOucrr.add(0.0);
            }
        }
        TextObject textObject = new TextObject(orderTextOucrr, sentiment);

        MongoDbController.getInstance().addTextObjectTest(textObject, text);
//
//        String json = gson.toJson(car);
//        Helper.getInstance().writeInstanceDataToCsv(object, "instancesPromoTest.csv");
//        Instance object = textObject.getMLInstance();

        return textObject;
    }


    public List<TextObject> createInstances(List<Text> texts, Dictionary dictionary, Map<String, Sentiment> sentiments) {
        List<TextObject> objects = new ArrayList<TextObject>();

        for (Text text : texts) {
            ArrayList<Double> wordOccurLst = dictionary.getOrderTextWordsOccur(text);
            Sentiment textSentiment = sentiments.get(text.getId());

            TextObject textObject = new TextObject(wordOccurLst, textSentiment, text.getType());

            objects.add(textObject);
        }

        return objects;
    }

    public List<TextObject> createInstances(List<Text> texts, Map<String, Sentiment> sentiments) {
        List<TextObject> objects = new ArrayList<TextObject>();

        for (Text text : texts) {
            Sentiment textSentiment = sentiments.get(text.getId());

            TextObject textObject = new TextObject(textSentiment, text.getType());

            objects.add(textObject);
        }

        return objects;
    }

    public Dictionary getFilteredDictionary(Dictionary dictionary) {
//        Set<String> dictionaryWords = dictionary.getDictionaryWords();
        List<String> dictionaryWords = MongoDbController.getInstance().getDictionaryWord();
        Set<String> newDictionaryWords = new HashSet<>();
        Map<String, Map<String, Integer>> perExOccur = dictionary.getPerExOccur();
        Map<String, Map<String, Integer>> promoOccur = dictionary.getPromoOccur();
        Set<String> wordsToRemove = new HashSet<>();
        List<Integer> rejectionWordsIndexs = new ArrayList<>();

        for (int i = 0; i < dictionaryWords.size(); i++) {
            String word = dictionaryWords.get(i);
            Map<String, Integer> perExMap = perExOccur.get(word);
            Map<String, Integer> promoMap = promoOccur.get(word);

            Collection<Integer> perExVecWord = perExMap.values();
            Collection<Integer> promoVecWord = promoMap.values();

            int totPerExOccurr = getTotalOccur(perExVecWord);
            int totPromoOccurr = getTotalOccur(promoVecWord);

            boolean isWordDeletedByWordOccurTH = false;
            if (totPerExOccurr < Settings.wordOccurTH && totPromoOccurr < Settings.wordOccurTH) {
                wordsToRemove.add(word);
                isWordDeletedByWordOccurTH = true;
//                rejectionWordsIndexs.add(i);
            }

            if (!isWordDeletedByWordOccurTH) {
                SpearmanRankCorrelation spearmanRankCorr = new SpearmanRankCorrelation();

                Instance perExInstance = new DenseInstance(perExVecWord.stream().mapToDouble(d -> d).toArray(), TextTypeEnum.PersonalExperience.toString());
                Instance promoInstance = new DenseInstance(promoVecWord.stream().mapToDouble(d -> d).toArray(), TextTypeEnum.Promotion.toString());

                double dest = spearmanRankCorr.measure(perExInstance, promoInstance);

//                if (Math.abs(dest) < Settings.spearmanRankCorrelationThreshold) {
//                    wordsToRemove.add(word);
//                    rejectionWordsIndexs.add(i);
//                }

                if (Math.abs(dest) < Settings.spearmanRankCorrelationThreshold) {
                    newDictionaryWords.add(word);
                    rejectionWordsIndexs.add(i);
                }
            }
        }

//        dictionaryWords.removeAll(wordsToRemove);
        return new Dictionary(perExOccur, promoOccur, newDictionaryWords,rejectionWordsIndexs);
    }

    public int getTotalOccur(Collection<Integer> values) {
        int counter = 0;

        for (Integer value : values) {
            if (value != null) {
                counter = counter + value;
            }
        }
        return counter;
    }


}
