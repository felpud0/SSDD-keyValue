package es.um.sisdist.models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MapReduceRequest {
    
    public String map;
    public String reduce;
    public String out_db;

}
