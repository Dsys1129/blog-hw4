package com.example.bloghw4.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bloghw4.post.entity.Post;

public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findAllByOrderByCreatedDateDesc();
}
