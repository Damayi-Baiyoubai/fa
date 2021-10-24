package com.example.webcrawlerstudy.dataobject;

import java.util.HashMap;
import java.util.Map;

public class ElementLabelHead extends ElementLabel{
    private Map<String,String> properties = new HashMap<>();
    private boolean beComplete = false;

    @Override
    public String toString() {
        return "ElementLabelHead{" +
        "name='" + name + '\'' +
                ", beginIndex=" + beginIndex +
                ", endIndex=" + endIndex +
                ", properties=" + properties +
                ", beComplete=" + beComplete +
                '}';
    }

    public boolean isBeComplete() {
        return beComplete;
    }

    public void setBeComplete(boolean beComplete) {
        this.beComplete = beComplete;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
