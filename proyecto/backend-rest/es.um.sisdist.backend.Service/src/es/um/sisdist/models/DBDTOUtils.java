package es.um.sisdist.models;

import java.util.HashMap;

import es.um.sisdist.backend.dao.models.DB;

public class DBDTOUtils {

    public static DB fromDTO(DBDTO dbdto) {
        HashMap<String,String> tables = new HashMap<String, String>();
        dbdto.getD().forEach(d -> tables.put(d.getK(), d.getV()));
        return new DB(dbdto.getDbname(), tables);
    }

    public static DBDTO toDTO(DB db) {
        DBDTO dbdto = new DBDTO();
        dbdto.setDbname(db.getDbname());
        db.getTables().forEach((k,v) -> dbdto.getD().add(new D(k,v)));
        return dbdto;
    }

}
