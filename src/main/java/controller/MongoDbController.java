package controller;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.util.JSON;
import controller.algorithm.SentimentBuilder;
import controller.utils.Logger;
import controller.utils.TextAdapter;
import controller.utils.TextTypeEnum;
import model.*;
import model.Dictionary;

import java.net.UnknownHostException;
import java.util.*;

public class MongoDbController {
    // static variable single_instance of type Singleton
    private static MongoDbController single_instance = null;

    private final String mongoServerURI = "mongodb://localhost:27017";
    private final String databaseName = "tripo";
    private final String dictionaryWordsCollection = "dictionary_words2";
    private final String dictionaryCollection = "dictionary2";
    private final String testObjectCollection = "text_object_test3";
    private final String testTypeObjectCollection = "text_object_test_type3";
    private final String textCollection = "texts";
    private String testTexts = "text_test";
    private DB database;

    // static method to create instance of Singleton class
    public static MongoDbController getInstance() {
        if (single_instance == null)
            single_instance = new MongoDbController();

        return single_instance;
    }

    // private constructor restricted to this class itself
    private MongoDbController() {
        // Creating a Mongo client
        try {
            MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoServerURI));
            database = mongoClient.getDB(databaseName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public List<Text> getTextsByType(TextTypeEnum type) {
        return this.getTexts(type, textCollection);
    }

    public List<Text> getTextsAll() {
        return this.getTexts(null, textCollection);
    }

    public List<Text> getTestTexts(TextTypeEnum type) {
        return this.getTexts(type, testTexts);
    }

    public List<Text> getTestTexts(TextTypeEnum type, List<String> aList) {
        List<Text> text = getTestTexts(type);
        List<Text> newText = new ArrayList<>();

        for (Text text1 : text) {
            if (aList.contains(text1.getLink())) {
                newText.add(text1);
            }
        }
        return newText;
    }

    private List<Text> getTexts(TextTypeEnum type, String collect) {
        DBCollection collection = database.getCollection(collect);
        List<Text> tempTextsList = new ArrayList<Text>();
        tempTextsList.clear();


        DBCursor texts;
        if (type != null) {
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("type", type.toString());
            texts = collection.find(searchQuery);
        } else {
            texts = collection.find();
        }

        while (texts.hasNext()) {
            DBObject item = texts.next();

            Text text = new Text(
                    item.get("_id").toString(),
                    (String) item.get("link"),
                    (String) item.get("content"),
                    TextTypeEnum.valueOf(((String) item.get("type"))));

            tempTextsList.add(text);
        }

        return tempTextsList;
    }

    public List<String> getStopWords() {
        DBCollection collection = database.getCollection("stop_words");
        List<String> stopWordsList = new ArrayList<>();

        DBCursor sets = collection.find();

        while (sets.hasNext()) {
            stopWordsList.addAll((List<String>) sets.next().get("set1"));
        }

        return stopWordsList;
    }

    public void addText(Text text) {
        addTextByCollection(text, textCollection);
    }

    public void addTextByCollection(Text text, String collect) {
//        Logger.debug(text.toString());

        DBCollection collection = database.getCollection(collect);
        DBObject dbObject = (DBObject) JSON.parse(text.getJsonFormat());
        collection.insert(dbObject);
    }

    public void addDictionaryWords(Set<String> dictionaryWords) {
        database.getCollection(dictionaryWordsCollection).drop();
        DBCollection collection = database.getCollection(dictionaryWordsCollection);


        String json = "{" +
                "\"words\":" + listToJson(dictionaryWords) +
                "}";

        Logger.debug("MongoDb DBObject:" + json);
        DBObject dbObject = (DBObject) JSON.parse(json);

        WriteResult res = collection.insert(WriteConcern.SAFE, dbObject);
        Logger.info("MongoDb insertion result:" + res);
    }

    public void addDictionary(Dictionary dictionary) {
        database.getCollection(dictionaryCollection).drop();
        DBCollection collection = database.getCollection(dictionaryCollection);
        TextAdapter textAdapter = TextAdapter.getInstance();
        Set<String> dictionaryWords = dictionary.getDictionaryWords();
        Map<String, Map<String, Integer>> perExOccur = dictionary.getPerExOccur();
        Map<String, Map<String, Integer>> promoOccur = dictionary.getPromoOccur();

        String json;

        for (String word : dictionaryWords) {
            json = "{";
            List<DataItem> dataItemVecPerEx = textAdapter.getDataItemByWord(perExOccur, word);
            List<DataItem> dataItemVecPromo = textAdapter.getDataItemByWord(promoOccur, word);

            json += ("_id: \"" + word + "\"," +
                    " \"ls\": {" +
                    "\"" + TextTypeEnum.PersonalExperience + "\": " +
                    "       [" +
                    dataItemToJson(dataItemVecPerEx) +
                    "       ]," +
                    "\"" + TextTypeEnum.Promotion + "\": " +
                    "       [" +
                    dataItemToJson(dataItemVecPromo) +
                    "       ]" +
                    "   } " +
                    "}"

            );

            DBObject dbObject = (DBObject) JSON.parse(json);
            collection.insert(WriteConcern.SAFE, dbObject);
        }
    }

    private String dataItemToJson(List<DataItem> dataItem) {
        String json = "";

        for (DataItem item : dataItem) {
            json += ("{\"" + item.getId() + "\": " + item.getValue() + "},");
        }

        json = json.substring(0, json.lastIndexOf(","));
        return json;
    }

    private String listToJson(Set<String> list) {
        String json = "[";

        for (String word : list) {
            json += "\"" + word + "\",";
        }

        json = json.substring(0, json.lastIndexOf(","));
        json += "]";

        return json;
    }

    public List<String> getDictionaryWord() {
        DBCollection collection = database.getCollection(dictionaryWordsCollection);
        DBCursor arrObject = collection.find();
//        List<String> res = new ArrayList<>();

//        while (arrObject.hasNext()) {
//            DBObject item = arrObject.next();
//            String word = (String) item.get("_id");
//            res.add(word);
//        }
        DBObject item = arrObject.next();
        List res = (List) item.get("words");
        return res;
    }

    public Dictionary getDictionary() {
        DBCollection collection = database.getCollection(dictionaryCollection);
        DBCursor wordItem = collection.find();
        List<String> res = null;
        Set<String> dictionaryWord = new HashSet<>(this.getDictionaryWord());
        Map<String, Map<String, Integer>> perExOccur = new HashMap<>();
        Map<String, Map<String, Integer>> promoOccur = new HashMap<>();

        while (wordItem.hasNext()) {
            DBObject item = wordItem.next();
            String word = (String) item.get("_id");
            item = (DBObject) item.get("ls");
            List<DBObject> perExOccurObj = (List<DBObject>) item.get(TextTypeEnum.PersonalExperience.toString());
            Map<String, Integer> perExOccurMap = new HashMap<>();

            for (DBObject val : perExOccurObj) {
                for (String key : val.keySet()) {
                    perExOccurMap.put(key, (Integer) val.get(key));
                }
            }

            perExOccur.put(word, perExOccurMap);

            List<DBObject> promoOccurObj = (List<DBObject>) item.get(TextTypeEnum.Promotion.toString());
            Map<String, Integer> promoOccurMap = new HashMap<>();

            for (DBObject val : promoOccurObj) {
                for (String key : val.keySet()) {
                    promoOccurMap.put(key, (Integer) val.get(key));
                }
            }

            promoOccur.put(word, promoOccurMap);
        }

        Dictionary dictionary = new Dictionary(perExOccur, promoOccur, dictionaryWord);

        return dictionary;
    }

    public Map<String, Sentiment> getTextSentiments() {
        SentimentBuilder sentimentBuilder = new SentimentBuilder();
        Map<String, Sentiment> textSentiments = sentimentBuilder.load();
        return textSentiments;
    }

    public void addTextObjectTest(TextObject textObject, Text text) {
        Gson gson = new Gson();
        String json = gson.toJson(textObject);

        DBCollection collection = database.getCollection(testObjectCollection);
        DBObject dbObject = (DBObject) JSON.parse(json);
        collection.insert(dbObject);

        DBCollection collection2 = database.getCollection(testTypeObjectCollection);
        String json2 = "{" +
                "\"_id\": {" +
                "\"$oid\": \"" + dbObject.get("_id").toString() + "\"" +
                "    }," +
                "\"type\": \"" + text.getType() + "\"," +
                "\"link\": \"" + text.getLink() + "\"}";
        Logger.info(json2);
        DBObject dbObjectType = (DBObject) JSON.parse(json2);
        collection2.insert(dbObjectType);
    }


    public List<TextObject> getTestObjects() {
        DBCollection collection = database.getCollection(testObjectCollection);
        DBCursor object = collection.find();
        Gson gson = new Gson();

        List<TextObject> textObjectList = new ArrayList<>();

        while (object.hasNext()) {
            DBObject item = object.next();

            Sentiment sentiment = gson.fromJson(item.get("sentiment").toString(), Sentiment.class);
            List wordsOccur = gson.fromJson(item.get("wordsOccur").toString(), List.class);

            TextObject textObject = new TextObject(item.get("_id").toString(), wordsOccur, sentiment);
            textObjectList.add(textObject);
        }
        return textObjectList;
    }

    public Map<String, String> getTestObjectsTypes() {
        DBCollection collection = database.getCollection(testTypeObjectCollection);
        DBCursor object = collection.find();
        Gson gson = new Gson();

        Map<String, String> textObjectList = new HashMap<>();

        while (object.hasNext()) {
            DBObject item = object.next();
            textObjectList.put(item.get("_id").toString(), item.get("type").toString());
        }

        return textObjectList;
    }


}
