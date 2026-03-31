package com.kymokim.desktopdictionary.answer.repository;

import com.kymokim.desktopdictionary.answer.entity.Answer;
import com.kymokim.desktopdictionary.article.entity.Article;
import com.kymokim.desktopdictionary.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllByQuestion(Question question);

    List<Answer> findAllByIsReportedTrue();
}
