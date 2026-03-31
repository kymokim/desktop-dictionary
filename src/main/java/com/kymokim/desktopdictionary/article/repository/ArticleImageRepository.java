package com.kymokim.desktopdictionary.article.repository;

import com.kymokim.desktopdictionary.article.entity.Article;
import com.kymokim.desktopdictionary.article.entity.ArticleImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleImageRepository extends JpaRepository<ArticleImage, Long> {

}
