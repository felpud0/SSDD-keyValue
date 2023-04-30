package es.um.sisdist.backend.dao.models;

public class Pair {
    private String key;
    private String value;

    public Pair(String name, String type) {
        this.key = name;
        this.value = type;
    }

    public Pair() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Pair [name=" + key + ", type=" + value + "]";
    }
}