package com.kymokim.desktopdictionary.question.repository;

import com.kymokim.desktopdictionary.article.entity.Article;
import com.kymokim.desktopdictionary.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    List<Question> findAllByIsReportedTrue();

    Page<Question> findAll(Pageable pageable);

    Page<Question> findAllByCategory(String category, Pageable pageable);
}
