package es.um.sisdist.models;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class D{
    private String k;
    private String v;

    public D(String k, String v) {
        this.k = k;
        this.v = v;
    }

    public D() {
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}