package com.example.webcrawlerstudy.service;

import com.example.webcrawlerstudy.dataobject.ElementInfo;
import com.example.webcrawlerstudy.dataobject.ElementLabel;
import com.example.webcrawlerstudy.dataobject.ElementLabelEnd;
import com.example.webcrawlerstudy.dataobject.ElementLabelHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class ElementReader {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private StringBuilder htmlContent;
    private List<ElementInfo> elementInfoList = new ArrayList<>();

    public StringBuilder getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(StringBuilder htmlContent) {
        this.htmlContent = htmlContent;
    }

    public ElementInfo getElementInfo(String name) {
        return this.getElementInfo(name, 0);
    }

    public ElementInfo getElementInfo(String name, int beginIndex) {
        int indexOfElementBeginLeft = beginIndex - 1;
        do {
            if ((indexOfElementBeginLeft + 2) > htmlContent.length()) {
                indexOfElementBeginLeft = -1;
            } else {
                indexOfElementBeginLeft = htmlContent.indexOf("<" + name, indexOfElementBeginLeft + 1);
                logger.debug("" + indexOfElementBeginLeft);
            }
        } while (indexOfElementBeginLeft != -1 && (htmlContent.charAt(indexOfElementBeginLeft + name.length() + 1) != (' ')
                && htmlContent.charAt(indexOfElementBeginLeft + name.length() + 1) != '>'
        ));

        if (indexOfElementBeginLeft == -1) {
            return null;
        }

        //没有考虑到元素标签可能有大于号">"的情况
        int indexOfElementBeginRight = htmlContent.indexOf(">", indexOfElementBeginLeft + 1);
        logger.debug("方法getElementInfo: 标签头内的内容为：" + htmlContent.substring(indexOfElementBeginLeft, indexOfElementBeginRight + 1));
        int indexOfElementEnd = htmlContent.indexOf("</" + name + ">") + 3 + name.length();

        ElementInfo elementInfo = new ElementInfo();
        elementInfo.setName(name);
        String content = htmlContent.substring(indexOfElementBeginRight + 1, indexOfElementEnd - 3 - name.length());
        elementInfo.setContent(new StringBuilder(content));
        logger.debug("方法getElementInfo: 获得的标签信息对象为：" + elementInfo);
        return elementInfo;
    }

    public List<ElementLabel> getAllElementLabel() {
        List<ElementLabel> elementLabelList = new LinkedList<>();
        for (int index = 0; index < htmlContent.length(); index++) {
            if (htmlContent.charAt(index) == '<') {
                index++;
                if (index < htmlContent.length() && Character.isLetter(htmlContent.charAt(index))) {
                    ElementLabelHead elementLabelHead = new ElementLabelHead();
                    elementLabelHead.setBeginIndex(index - 1);
                    for (index++; index < htmlContent.length(); index++) {
                        if (htmlContent.charAt(index) == '<') {
                            elementLabelHead.setEndIndex(index - 1);
                            index--;
                            break;
                        } else if (htmlContent.charAt(index) == '>') {
                            elementLabelHead.setEndIndex(index);
                            break;
                        }
                    }
                    if (index >= htmlContent.length()) {
                        elementLabelHead.setEndIndex(htmlContent.length() - 1);
                    }
                    setElementLabelHeadProperties(elementLabelHead);
                    elementLabelList.add(elementLabelHead);
                } else if (index < htmlContent.length() && (htmlContent.charAt(index) == '/')) {
                    ElementLabelEnd elementLabelEnd = new ElementLabelEnd();
                    elementLabelEnd.setBeginIndex(index - 1);
                    for (index++; index < htmlContent.length(); index++) {
                        if (htmlContent.charAt(index) == '<') {
                            elementLabelEnd.setEndIndex(index - 1);
                            break;
                        } else if (htmlContent.charAt(index) == '>') {
                            elementLabelEnd.setEndIndex(index);
                            break;
                        }
                    }
                    if (index >= htmlContent.length()) {
                        elementLabelEnd.setEndIndex(htmlContent.length() - 1);
                    }
                    elementLabelEnd.setName(htmlContent.substring(elementLabelEnd.getBeginIndex() + 2, elementLabelEnd.getEndIndex()));
                    elementLabelList.add(elementLabelEnd);
                }
            }
        }
        return elementLabelList;
    }

    public List<ElementInfo> read(){
        List<ElementInfo> elementInfoes = this.getElementInfoes();
        this.setElementInfoesContent(elementInfoes);
        return elementInfoes;
    }

    public List<ElementInfo> getElementInfoes() {
        List<ElementLabel> allElementLabel = this.getAllElementLabel();
        ElementInfoPackager elementInfoPackager = new ElementInfoPackager(allElementLabel.iterator());
        ElementInfo elementInfo = elementInfoPackager.packageElementInfo(new ElementInfo());
        List<ElementInfo> sonElementInfos = elementInfo.getSonElementInfos();
        return sonElementInfos;
    }

    public void setElementInfoesContent(List<ElementInfo> elementInfoList) {
        Iterator<ElementInfo> iterator = elementInfoList.iterator();
        int contentIndex;
        while (iterator.hasNext()) {
            ElementInfo next = iterator.next();
            if (next.getElementLabelEnd() != null) {
                contentIndex = next.getElementLabelHead().getEndIndex();
                if (next.getSonElementInfos().size() > 0) {
                    setElementInfoesContent(next.getSonElementInfos());
                    Iterator<ElementInfo> SonElementInfositerator = next.getSonElementInfos().iterator();
                    while(SonElementInfositerator.hasNext()){
                        ElementInfo sonNext = SonElementInfositerator.next();
                        if(sonNext.getElementLabelEnd() == null){
                            if ((contentIndex + 1) < sonNext.getElementLabelHead().getBeginIndex())
                                next.getContent().append(htmlContent.substring(contentIndex + 1, sonNext.getElementLabelHead().getBeginIndex()));
                            contentIndex = sonNext.getElementLabelHead().getEndIndex();
                        }else {
                            if ((contentIndex + 1) < sonNext.getElementLabelHead().getBeginIndex())
                                next.getContent().append(htmlContent.substring(contentIndex + 1, sonNext.getElementLabelHead().getBeginIndex()));
                            contentIndex = sonNext.getElementLabelEnd().getEndIndex();
                        }

                    }
                    if ((contentIndex + 1) < next.getElementLabelEnd().getBeginIndex())
                        next.getContent().append(htmlContent.substring(contentIndex + 1, next.getElementLabelEnd().getBeginIndex()));
                } else {
                    if ((contentIndex + 1) < next.getElementLabelEnd().getBeginIndex())
                        next.getContent().append(htmlContent.substring(contentIndex + 1, next.getElementLabelEnd().getBeginIndex()));
                }
            } else {

            }
        }
    }

    public class ElementInfoPackager {
        public Iterator<ElementLabel> iterator;
        ElementLabel elementLabel = null;
        boolean elementInfoHasEnd = true;
        Logger logger = LoggerFactory.getLogger(this.getClass());

        public ElementInfoPackager(Iterator<ElementLabel> iterator) {
            this.iterator = iterator;
        }

        public ElementInfo packageElementInfo(ElementInfo elementInfo) {
            while (true) {
                if (elementInfoHasEnd || (elementLabel == null)) {
                    if (iterator.hasNext()) {
                        elementLabel = iterator.next();
                    } else {
                        return elementInfo;
                    }
                }
                if (elementLabel instanceof ElementLabelHead) {
                    logger.debug("-------elementLabel(" + elementLabel.getName() + ")是ElementLabelHead-------");
                    ElementInfo elementInfoVar = new ElementInfo();
                    elementInfoVar.setElementLabelHead((ElementLabelHead) elementLabel);
                    elementInfoVar.setName(elementLabel.getName());
                    elementInfoVar = packageElementInfo(elementInfoVar);
                    elementInfo.getSonElementInfos().add(elementInfoVar);
                    ElementReader.this.elementInfoList.add(elementInfoVar);
                    if (elementInfoVar.getElementLabelEnd() == null) {
                        logger.debug("elementInfoVar(" + elementInfoVar.getName() + ")没有ElementLabelEnd");
                        elementInfoHasEnd = false;
                        List<ElementInfo> list = elementInfoVar.getSonElementInfos();
                        if (elementInfoVar.getSonElementInfos() != null) {
                            for (ElementInfo i :
                                    list) {
                                elementInfo.getSonElementInfos().add(i);
                            }
                            elementInfoVar.getSonElementInfos().clear();
                        }
                    }
                } else if (elementLabel instanceof ElementLabelEnd) {
                    logger.debug("-------elementLabel(" + elementLabel.getName() + ")是ElementLabelEnd-------");
                    if (elementInfo.getElementLabelHead() == null || elementInfo.getElementLabelHead().getName().equals(elementLabel.getName())) {
                        if (elementInfo.getElementLabelHead() != null)
                            logger.debug("elementLabel(" + elementLabel.getName() + ")与elementInfo.getElementLabelHead()(" + elementInfo.getElementLabelHead().getName() + ")为一对");
                        elementInfo.setElementLabelEnd((ElementLabelEnd) elementLabel);
                        elementInfoHasEnd = true;
                        return elementInfo;
                    } else {
                        logger.debug("elementLabel(" + elementLabel.getName() + ")与elementInfo.getElementLabelHead()(" + elementInfo.getElementLabelHead().getName() + ")不为一对");
                        elementInfoHasEnd = false;
                        return elementInfo;
                    }
                }
            }
        }
    }

    public void setElementLabelHeadProperties(ElementLabelHead elementLabelHead) {
        int nameDivIndex;
        for (nameDivIndex = elementLabelHead.getBeginIndex() + 1; nameDivIndex <= elementLabelHead.getEndIndex(); nameDivIndex++) {
            if (htmlContent.charAt(nameDivIndex) == ' ') {
                if (elementLabelHead.getName() == null)
                    elementLabelHead.setName(htmlContent.substring(elementLabelHead.getBeginIndex() + 1, nameDivIndex));
                nameDivIndex = filterDivSpaceAfter(nameDivIndex, elementLabelHead.getEndIndex());
                for (int i = nameDivIndex + 1; i <= elementLabelHead.getEndIndex(); i++) {
                    if (htmlContent.charAt(i) == '=') {
                        String key = htmlContent.substring(nameDivIndex + 1, filterDivSpaceBefore(i, elementLabelHead.getBeginIndex()));
                        String value = null;
                        i = filterDivSpaceAfter(i, elementLabelHead.getEndIndex());
                        for (int j = i + 1; j <= elementLabelHead.getEndIndex(); j++) {
                            if (htmlContent.charAt(j) == ' ' || htmlContent.charAt(j) == '<' || htmlContent.charAt(j) == '>' ||
                                    (htmlContent.charAt(j) == '/' && htmlContent.charAt(j + 1) == '>')) {
                                value = htmlContent.substring(i + 1, j);
                                nameDivIndex = j - 1;
                                break;
                            }
                        }
                        elementLabelHead.getProperties().put(key, value);
                        break;
                    }
                }
            } else if (htmlContent.charAt(nameDivIndex) == '>') {
                if (htmlContent.charAt(nameDivIndex - 1) == '/') {
                    if (elementLabelHead.getName() == null)
                        elementLabelHead.setName(htmlContent.substring(elementLabelHead.getBeginIndex() + 1, elementLabelHead.getEndIndex() - 1));
                    elementLabelHead.setBeComplete(true);
                    break;
                } else {
                    if (elementLabelHead.getName() == null)
                        elementLabelHead.setName(htmlContent.substring(elementLabelHead.getBeginIndex() + 1, elementLabelHead.getEndIndex()));
                }
            } else if (nameDivIndex == elementLabelHead.getEndIndex()) {
                if (elementLabelHead.getName() == null)
                    elementLabelHead.setName(htmlContent.substring(elementLabelHead.getBeginIndex() + 1, elementLabelHead.getEndIndex() + 1));
                break;
            }
        }


    }

    public int filterDivSpaceAfter(int divIndex, int endIndex) {
        for (int i = divIndex + 1; i <= endIndex; i++) {
            if (htmlContent.charAt(i) == ' ') {
                divIndex++;
            } else {
                break;
            }
        }
        return divIndex;
    }

    public int filterDivSpaceBefore(int divIndex, int endIndex) {
        for (int i = divIndex - 1; i > endIndex; i--) {
            if (htmlContent.charAt(i) == ' ') {
                divIndex--;
            } else {
                break;
            }
        }
        return divIndex;
    }

    public List<ElementInfo> getElementInfoList() {
        return elementInfoList;
    }

    public void setElementInfoList(List<ElementInfo> elementInfoList) {
        this.elementInfoList = elementInfoList;
    }

    public ElementReader() {

    }

    public ElementReader(StringBuilder htmlContent) {
        this.htmlContent = htmlContent;
    }
}
