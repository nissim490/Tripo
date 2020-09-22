package model;

public class DataItem {
    String id;
    int value;

    public DataItem(String id, int value) {
        this.id = id;
        this.value = value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public int getValue() {
        return value;
    }
}
