package com.example.webcrawlerstudy.dataobject;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ElementInfo {
    private String name;
    private ElementLabelHead elementLabelHead;
    private ElementLabelEnd elementLabelEnd;
    private StringBuilder content = new StringBuilder();
    private List<ElementInfo> sonElementInfos = new ArrayList<>();

    @Override
    public String toString() {
        return "ElementInfo{" +
                "name='" + name + '\'' +
                ",\n elementLabelHead=" + elementLabelHead +
                ",\n elementLabelEnd=" + elementLabelEnd +
                ",\n content='" + content + '\'' +
                ",\n sonElementInfos=" + sonElementInfos +
                "}\n";
    }

    public ElementLabelHead getElementLabelHead() {
        return elementLabelHead;
    }

    public void setElementLabelHead(ElementLabelHead elementLabelHead) {
        this.elementLabelHead = elementLabelHead;
    }

    public ElementLabelEnd getElementLabelEnd() {
        return elementLabelEnd;
    }

    public void setElementLabelEnd(ElementLabelEnd elementLabelEnd) {
        this.elementLabelEnd = elementLabelEnd;
    }

    public List<ElementInfo> getSonElementInfos() {
        return sonElementInfos;
    }

    public void setSonElementInfos(List<ElementInfo> sonElementInfos) {
        this.sonElementInfos = sonElementInfos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StringBuilder getContent() {
        return content;
    }

    public void setContent(StringBuilder content) {
        this.content = content;
    }
}
