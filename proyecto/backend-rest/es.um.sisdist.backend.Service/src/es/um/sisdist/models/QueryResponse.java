package es.um.sisdist.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class QueryResponse {
    public String dbname;
    public String pattern;
    public int page;
    public int perpage;
    public List<D> d;
}
