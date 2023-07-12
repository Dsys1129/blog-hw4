package com.example.bloghw4.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bloghw4.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
