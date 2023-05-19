package es.um.sisdist.models;

import java.util.ArrayList;
import java.util.List;

import es.um.sisdist.backend.dao.models.DB;
import es.um.sisdist.backend.dao.models.Pair;

public class DBDTOUtils {

    public static DB fromDTO(DBDTO dbdto) {
        System.out.println("DBDTOUtils.fromDTO: " + dbdto.toString());
        List<Pair> tables = new ArrayList<Pair>();
        if (dbdto.getD() == null) {
            dbdto.setD(new ArrayList<D>());
        }
        dbdto.getD().forEach(d -> tables.add(new Pair(d.getK(), d.getV())));
        return new DB(dbdto.getDbname(), tables);
    }

    public static DBDTO toDTO(DB db) {
        System.out.println("DBDTOUtils.toDTO: " + db.toString());
        DBDTO dbdto = new DBDTO();
        dbdto.setDbname(db.getDbname());
        ArrayList<D> ds = new ArrayList<D>();
        db.getTables().forEach((pair) -> ds.add(new D(pair.getKey(), pair.getValue())));
        dbdto.setD(ds);
        return dbdto;
    }

}
