package model;

import controller.utils.TextTypeEnum;

public class Text {
    private String id;
    private String link;
    private String content;
    private TextTypeEnum type;
    private int numOfWords;
    private int numOfSentences;

    public Text(String link, String content, TextTypeEnum type) {
        this.link = link;
        this.content = content;
        this.type = type;
    }

    public Text(String id, String link, String content, TextTypeEnum type) {
        this.id = id;
        this.link = link;
        this.content = content;
        this.type = type;
    }

    public Text(String link, String content) {
        this.link = link;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getLink() {
        return link;
    }

    public TextTypeEnum getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Text{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getJsonFormat() {
        return "{" +
                "\"link\":\"" + link + "\"" +
                ", \"content\":\"" + getJsonFriendly(content) + "\"" +
                ", \"type\":\"" + type + "\"" +
                "}";

    }

    private String getJsonFriendly(String original) {
        return original.replaceAll("\"", "\\\"")
                .replaceAll("\b", "\\b")
                .replaceAll("\f", "\\f")
                .replaceAll("\n", "\\n")
                .replaceAll("\r", "\\r")
                .replaceAll("\t", "\\t")
                .replaceAll("\"", "")
                .replaceAll(":", "")
                .replaceAll(";", "")
                .replaceAll(",", "")
                .replaceAll("'", "")
                .replaceAll("“", "")
                .replaceAll("\\)", "")
                .replaceAll("\\(", "")
                .replaceAll("\\{", "")
                .replaceAll("\\}", "")
                .replaceAll("\\[", "")
                .replaceAll("\\]", "");
    }

    public void setNumOfWords(int size) {
        this.numOfWords = size;
    }

    public void setNumOfSentences(int size) {
        this.numOfSentences = size;
    }

    public int getNumOfSentences() {
        return numOfSentences;
    }

    public int getNumOfWords() {
        return numOfWords;
    }
}
