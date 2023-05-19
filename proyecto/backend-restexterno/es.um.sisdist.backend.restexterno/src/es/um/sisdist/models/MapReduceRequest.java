package es.um.sisdist.models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MapReduceRequest {
    
    public String map;
    public String reduce;
    public String out_db;

    @Override
    public String toString() {
        return "MapReduceRequest [map=" + map + ", out_db=" + out_db + ", reduce=" + reduce + "]";
    }

}
