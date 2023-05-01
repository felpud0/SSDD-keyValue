package es.um.sisdist.backend.dao.models;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DB {

    private String dbname;
    private List<Pair> tables;
    
    public DB(String dbname, List<Pair> tables) {
        this.dbname = dbname;
        this.tables = tables;
    }

    public DB() {
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String name) {
        this.dbname = name;
    }

    public List<Pair> getTables() {
        return tables;
    }

    public void setTables(List<Pair> tables) {
        this.tables = tables;
    }

    public void addPair(String key, String value) {
        tables.add(new Pair(key, value));
    }

    public Optional<Pair> getPair(String key) {
        return tables.stream().filter(p -> p.getKey().equals(key)).findFirst();
    }

    public boolean updatePair(String key, String value) {
        Optional<Pair> pair = getPair(key);
        if (pair.isPresent()) {
            pair.get().setValue(value);
            pair.get().setKey(key);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "DB [dbname=" + dbname + ", tables=" + tables + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof DB))
            return false;
        DB other = (DB) obj;
        return dbname.equals(other.dbname);
    }
}
