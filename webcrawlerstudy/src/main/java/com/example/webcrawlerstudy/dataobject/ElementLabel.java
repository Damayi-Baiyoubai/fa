package com.example.webcrawlerstudy.dataobject;

public class ElementLabel {
    protected String name;
    protected int beginIndex;
    protected int endIndex;

    @Override
    public String toString() {
        return "ElementLabel{" +
                "name='" + name + '\'' +
                ", beginIndex=" + beginIndex +
                ", endIndex=" + endIndex +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
}
