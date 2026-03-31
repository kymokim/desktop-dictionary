package com.kymokim.desktopdictionary.usedPost.repository;

import com.kymokim.desktopdictionary.usedPost.entity.UsedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsedPostRepository extends JpaRepository<UsedPost, Long> {

    Page<UsedPost> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    List<UsedPost> findAllByIsReportedTrue();

    Page<UsedPost> findAll(Pageable pageable);

    Page<UsedPost> findAllByCategory(String category, Pageable pageable);
}
