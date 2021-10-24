package com.example.webcrawlerstudy.service;

import com.example.webcrawlerstudy.dao.NewsMapper;
import com.example.webcrawlerstudy.dataobject.ElementInfo;
import com.example.webcrawlerstudy.dataobject.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

@Service
public class NewsService {
    ElementReader elementReader = new ElementReader();
    News news = new News();
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
        elementReader.read();
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
            if (elementInfo.getName().equals("p")) {
                if (elementInfo.getElementLabelHead().getProperties().isEmpty())
                    contentSB.append(elementInfo.getContent());
            }
        }
        news.setContent(contentSB.toString());

        for (ElementInfo elementInfo : elementInfoList) {

            if (elementInfo.getName().equals("a")) {
                String sClass = elementInfo.getElementLabelHead().getProperties().get("class");
                if (sClass != null && (sClass.equals("\"index-module_itemTitle_39czT\"") || sClass.equals("\"index-module_newsTitle_2soHQ\""))) {
                    if (elementInfo.getElementLabelHead().getProperties().get("href") != null) {
                        String href = elementInfo.getElementLabelHead().getProperties().get("href");
                        if(href.charAt(0) == '\"' && href.charAt(href.length() - 1) == '\"'){
                            href = href.substring(1,href.length() - 1);
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

    public void add(News news){
        newsMapper.add(news);
    }

    public News getByUrlPath(String urlPath){
        return newsMapper.getByUrlPath(urlPath);
    }
}
