package com.example.webcrawlerstudy;


import com.example.webcrawlerstudy.dao.NewsMapper;
import com.example.webcrawlerstudy.dataobject.*;
import com.example.webcrawlerstudy.service.Crawler;
import com.example.webcrawlerstudy.service.ElementReader;
import com.example.webcrawlerstudy.service.NewsService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


@SpringBootTest
class WebcrawlerstudyApplicationTests {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    NewsService newsService;
    @Autowired
    NewsMapper newsMapper;
    @Autowired
    Crawler crawler;

    @Test
    void contextLoads() throws IOException {
        URL url = new URL("https://baijiahao.baidu.com/s?id=1713842485097489122&wfr=spider&for=pc");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        char[] buffer = new char[1024];
        int length = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while ((length = inputStreamReader.read(buffer)) != -1) {
            stringBuilder.append(buffer, 0, length);
        }
//        System.out.println(stringBuilder);

        NewsService newsService = new NewsService();
        ElementReader elementReader = new ElementReader(stringBuilder);
        newsService.setElementReader(elementReader);
        List<ElementLabel> allElementLabel = elementReader.getAllElementLabel();
        for (ElementLabel elementLabel:
                allElementLabel ) {
            if(elementLabel instanceof ElementLabelHead){
                ElementLabelHead elementLabelHead = (ElementLabelHead) elementLabel;
                System.out.println(elementLabelHead);
            }else {
                System.out.println(elementLabel);
            }
        }
        List<ElementInfo> elementInfoes = elementReader.getElementInfoes();
        elementReader.setElementInfoesContent(elementInfoes);
        newsService.assignNews(url);
        System.out.println(newsService.getNews());

        inputStreamReader.close();
        urlConnection.disconnect();
    }

    @Test
    public void test1() {
        StringBuilder htmlContent = new StringBuilder("<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title><title>html+css测试页面</title></title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<b></a></b>\n" +
                "</body>\n" +
                "\n" +
                "</html>");
        System.out.println(Character.isLetter('是'));
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
                    elementLabelHead.setName(htmlContent.substring(elementLabelHead.getBeginIndex() + 1, elementLabelHead.getEndIndex()));
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
        System.out.println(elementLabelList);
    }

    @Test
    public void test2() {
        StringBuilder htmlContent = new StringBuilder("<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>html+css测试页面</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "飞洒法师沙发\n" +
                "<div a=\"35\" b=\"65\">撒大声地萨达</div>\n" +
                "</body>\n" +
                "\n" +
                "</html>\n");
//        System.out.println((new ElementReader(htmlContent)).getAllElementLabel());
        ElementReader elementReader = new ElementReader(htmlContent);
        List<ElementLabel> allElementLabel = elementReader.getAllElementLabel();
        for (ElementLabel elementLabel:
                allElementLabel ) {
            if(elementLabel instanceof ElementLabelHead){
                ElementLabelHead elementLabelHead = (ElementLabelHead) elementLabel;
                System.out.println(elementLabelHead);
            }else {
                System.out.println(elementLabel);
            }
        }
        List<ElementInfo> elementInfoes = elementReader.getElementInfoes();
        elementReader.setElementInfoesContent(elementInfoes);
        System.out.println();
        System.out.println(elementInfoes);
    }

    @Test
    public void test34() throws MalformedURLException {
        URL url = new URL(
                "https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9452412278882794832%22%7D&n_type=1&p_from=4");
        crawler.crawl(url);
    }
    @Test
    public void test3455() throws MalformedURLException {
        URL url = new URL(
                "https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9327720002366507338%22%7D&n_type=1&p_from=4");
        News news = newsService.read(url);

        URL url2 = new URL(
                "https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_8487848514753656240%22%7D&n_type=1&p_from=3");
        News news2 = newsService.read(url2);
        System.out.println(news);
        System.out.println(news2);
    }
}
