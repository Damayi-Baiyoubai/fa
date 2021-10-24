package com.example.webcrawlerstudy.dao;

import com.example.webcrawlerstudy.dataobject.News;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NewsMapper {

    @Insert("INSERT INTO news (url_path,title,author,time,content) VALUES " +
            "(#{urlPath},#{title},#{author},#{time},#{content})")
    public void add(News news);

    @Select("SELECT * FROM news WHERE url_path=#{urlPath}")
    public News getByUrlPath(String urlPath);
}
