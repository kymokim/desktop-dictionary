package com.kymokim.desktopdictionary.comment.repository;

import com.kymokim.desktopdictionary.comment.entity.Comment;
import com.kymokim.desktopdictionary.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByArticle(Article article);

    List<Comment> findAllByIsReportedTrue();
}
