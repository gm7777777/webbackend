package com.gm.webbackend.dao;

import com.gm.webbackend.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestDao extends JpaRepository<Article,Long> {

    @Query("from Article a order by a.createTime desc")
     List<Article> getAllTest();
}
