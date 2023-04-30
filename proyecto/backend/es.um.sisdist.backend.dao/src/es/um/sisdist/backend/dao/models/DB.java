package es.um.sisdist.backend.dao.models;

import java.util.List;
import java.util.Map;

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
