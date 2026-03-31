package com.kymokim.desktopdictionary.article.repository;

import com.kymokim.desktopdictionary.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    List<Article> findAllByIsReportedTrue();

    Page<Article> findAll(Pageable pageable);

    Page<Article> findAllByCategory(String category, Pageable pageable);
}
