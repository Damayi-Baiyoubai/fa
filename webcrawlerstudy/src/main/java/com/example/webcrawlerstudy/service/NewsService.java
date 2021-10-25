package com.example.webcrawlerstudy.service;

import com.example.webcrawlerstudy.dao.NewsMapper;
import com.example.webcrawlerstudy.dataobject.ElementInfo;
import com.example.webcrawlerstudy.dataobject.ElementLabel;
import com.example.webcrawlerstudy.dataobject.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class NewsService {
    ElementReader elementReader = new ElementReader();
    List<ElementInfo> read;
    News news = null;
    @Autowired
    NewsMapper newsMapper;

    public ElementReader getElementReader() {
        return elementReader;
    }

    public void setElementReader(ElementReader elementReader) {
        this.elementReader = elementReader;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public void assignNews(URL url) {
        news = new News();
        read = elementReader.read();
        news.setUrlPath(url.toString());
        List<ElementInfo> elementInfoList = elementReader.getElementInfoList();
        for (ElementInfo elementInfo : elementInfoList) {
            if (elementInfo.getName().equals("title")) {
                news.setTitle(elementInfo.getContent().toString());
                break;
            }
        }

        for (ElementInfo elementInfo : elementInfoList) {
            if (elementInfo.getName().equals("span")) {
                String sClass = elementInfo.getElementLabelHead().getProperties().get("class");
                if (sClass != null && sClass.equals("\"index-module_accountAuthentication_3BwIx\"")) {
                    news.setAuthor(elementInfo.getContent().toString());
                }
            }
        }

        for (ElementInfo elementInfo : elementInfoList) {
            if (elementInfo.getName().equals("meta")) {
                String sItemprop = elementInfo.getElementLabelHead().getProperties().get("itemprop");
                if (sItemprop != null && sItemprop.equals("dateUpdate")) {
                    news.setTime(elementInfo.getElementLabelHead().getProperties().get("content"));
                }
            }
        }

        StringBuilder contentSB = new StringBuilder();
        for (ElementInfo elementInfo : elementInfoList) {
            if(elementInfo.getName().equals("body")){
                List<ElementInfo> stackEI = new LinkedList<>();
                stackEI.add(elementInfo);
                while (!stackEI.isEmpty()) {
                    ElementInfo elementInfo1 = stackEI.get(stackEI.size() - 1);
                    String name = elementInfo1.getName();
                    if ((name.equals("p") && (elementInfo1.getElementLabelHead().getProperties().get("class") == null ||
                            !elementInfo1.getElementLabelHead().getProperties().get("class").equals("\"index-module_authorName_7y5nA\"") ) )
                            || (name.length() == 2 && (name.charAt(0) == 'h') && Character.isDigit(name.charAt(1))) ) {
                        int indexOfHead = elementReader.getElementLabelList().indexOf(elementInfo1.getElementLabelHead());
                        int indexOfEnd = elementReader.getElementLabelList().indexOf(elementInfo1.getElementLabelEnd());
                        for (int i = indexOfHead; i < indexOfEnd; i++) {
                            ElementLabel elementLabelBegin = elementReader.getElementLabelList().get(i);
                            ElementLabel elementLabelEnd = elementReader.getElementLabelList().get(i + 1);
                            String substring = elementReader.getHtmlContent().substring(elementLabelBegin.getEndIndex() + 1, elementLabelEnd.getBeginIndex());
                            contentSB.append(substring);
                        }
                        stackEI.remove(stackEI.size() - 1);
                    } else {
                        stackEI.remove(stackEI.size() - 1);
                        for (int i = elementInfo1.getSonElementInfos().size() - 1; i >= 0; i--) {
                            stackEI.add(elementInfo1.getSonElementInfos().get(i));
                        }
                    }
                }
            }

        }
        news.setContent(contentSB.toString());

        for (ElementInfo elementInfo : elementInfoList) {

            if (elementInfo.getName().equals("a")) {
                String sClass = elementInfo.getElementLabelHead().getProperties().get("class");
                if (sClass != null && (sClass.equals("\"index-module_itemTitle_39czT\"") || sClass.equals("\"index-module_newsTitle_2soHQ\""))) {
                    if (elementInfo.getElementLabelHead().getProperties().get("href") != null) {
                        String href = elementInfo.getElementLabelHead().getProperties().get("href");
                        if (href.charAt(0) == '\"' && href.charAt(href.length() - 1) == '\"') {
                            href = href.substring(1, href.length() - 1);
                        }
                        news.getSonUrlPathes().add(href);
                    }
                }
            }
        }
    }

    public News read(URL url) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            char[] buffer = new char[1024];
            int length = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while ((length = inputStreamReader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, length);
            }
            elementReader.setHtmlContent(stringBuilder);
            assignNews(url);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStreamReader.close();
            } catch (Exception e) {

            }
            urlConnection.disconnect();
        }
        return news;
    }

    public void add(News news) {
        newsMapper.add(news);
    }

    public News getByUrlPath(String urlPath) {
        return newsMapper.getByUrlPath(urlPath);
    }
}
