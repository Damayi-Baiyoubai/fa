package com.example.webcrawlerstudy.service;

import com.example.webcrawlerstudy.dataobject.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

@Service
public class Crawler {
    @Autowired
    NewsService newsService;
    List<URL> urlList = new LinkedList<>();
    int count = 0;
    public void crawl(URL url){
        System.out.println(count);
        count++;
        if(urlList.isEmpty()){
            urlList.add(url);
        }
        if(newsService.getByUrlPath(url.toString()) == null){
            News news = newsService.read(url);
            for (String sonUrlPath:
                 news.getSonUrlPathes()) {
                try {
                    urlList.add(new URL(sonUrlPath));
                }catch (Exception e){
                    System.out.println("url异常");
                    e.printStackTrace();
                }
            }
            newsService.add(news);
            if(!urlList.isEmpty() && count < 100){
                this.crawl(urlList.get(0));
            }
        }else{
            urlList.remove(url);
            if(!urlList.isEmpty() && count < 100){
                this.crawl(urlList.get(0));
            }
        }
    }
}
