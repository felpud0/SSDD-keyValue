package es.um.sisdist.models;

import java.util.ArrayList;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DBDTO {
    
    @XmlElement(name = "dbname")
    private String dbname;
    @XmlElement(name = "d")
    private ArrayList<D> d;


    public DBDTO(String dbname, ArrayList<D> d) {
        this.dbname = dbname;
        this.d = d;
    }

    public DBDTO() {
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public ArrayList<D> getD() {
        return d;
    }

    public void setD(ArrayList<D> d) {
        this.d = d;
    }

    @Override
    public String toString() {
        return "DBDTO [name=" + dbname + ", tables=" + d + "]";
    }
}
