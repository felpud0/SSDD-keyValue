package es.um.sisdist.backend.dao.models;

import java.util.Map;

public class DB {

    private String dbname;
    private Map<String, String> tables;
    
    public DB(String dbname, Map<String, String> tables) {
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

    public Map<String, String> getTables() {
        return tables;
    }
}
