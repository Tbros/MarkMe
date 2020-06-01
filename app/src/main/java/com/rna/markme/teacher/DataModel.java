package com.rna.markme.teacher;

public class DataModel {
    String regno;
    Integer rss;
    Boolean doubt;

    public DataModel(String regno, Integer rss, Boolean doubt) {
        this.regno = regno;
        this.rss = rss;
        this.doubt = doubt;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public Integer getRss() {
        return rss;
    }

    public void setRss(Integer rss) {
        this.rss = rss;
    }

    public Boolean getDoubt() {
        return doubt;
    }

    public void setDoubt(Boolean doubt) {
        this.doubt = doubt;
    }
}
