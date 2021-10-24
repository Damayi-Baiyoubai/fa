package com.example.webcrawlerstudy.dataobject;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class News {
    String urlPath = null;
    String title = null;
    String author = null;
    String time = null;
    String content = null;
    List<String> sonUrlPathes = new ArrayList<>();

    @Override
    public String toString() {
        return "News{" +
                "urlPath='" + urlPath + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", time=" + time +
                ", content=" + content +
                ", sonUrlPathes=" + sonUrlPathes +
                '}';
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public void setTime(String time) {
        this.time = time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSonUrlPathes() {
        return sonUrlPathes;
    }

    public void setSonUrlPathes(List<String> sonUrlPathes) {
        this.sonUrlPathes = sonUrlPathes;
    }
}
