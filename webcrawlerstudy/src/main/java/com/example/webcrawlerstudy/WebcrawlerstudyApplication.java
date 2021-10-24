package com.example.webcrawlerstudy;

import com.example.webcrawlerstudy.service.Crawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication
public class WebcrawlerstudyApplication {
    @Autowired
    Crawler crawler;

    public static void main(String[] args) throws MalformedURLException {
        SpringApplication.run(WebcrawlerstudyApplication.class, args);
        WebcrawlerstudyApplication webcrawlerstudyApplication = new WebcrawlerstudyApplication();
        URL url = new URL(
                "https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9452412278882794832%22%7D&n_type=1&p_from=4");
        webcrawlerstudyApplication.crawler.crawl(url);
    }

}
