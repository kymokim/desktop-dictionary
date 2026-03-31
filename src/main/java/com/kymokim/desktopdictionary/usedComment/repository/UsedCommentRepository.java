package com.kymokim.desktopdictionary.usedComment.repository;

import com.kymokim.desktopdictionary.usedPost.entity.UsedPost;
import com.kymokim.desktopdictionary.usedComment.entity.UsedComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsedCommentRepository extends JpaRepository<UsedComment, Long> {
    List<UsedComment> findAllByUsedPost(UsedPost usedPost);

    List<UsedComment> findAllByIsReportedTrue();
}
