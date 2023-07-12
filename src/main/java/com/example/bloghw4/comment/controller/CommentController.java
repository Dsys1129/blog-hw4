package com.example.bloghw4.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bloghw4.comment.dto.CommentRequestDTO;
import com.example.bloghw4.comment.dto.CommentResponseDTO;
import com.example.bloghw4.comment.service.CommentService;
import com.example.bloghw4.global.BaseResponseDTO;
import com.example.bloghw4.global.jwtutil.LoginUser;
import com.example.bloghw4.global.jwtutil.UserDetails;
import com.example.bloghw4.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts/{postId}")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public ResponseEntity<CommentResponseDTO> createComment(@PathVariable Long postId, @RequestBody CommentRequestDTO commentRequestDTO, @LoginUser UserDetails userDetails,
    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        log.info("customUserDetails = {} {} {}", customUserDetails.getUser().getPassword(), customUserDetails.getUsername(), customUserDetails.getUsername());
        CommentResponseDTO response = commentService.createComment(postId, commentRequestDTO, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> modifyComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentRequestDTO commentRequestDTO,
        @LoginUser UserDetails userDetails) {
        CommentResponseDTO response = commentService.modifyComment(postId, commentId, commentRequestDTO, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<BaseResponseDTO> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @LoginUser UserDetails userDetails) {
        BaseResponseDTO response = commentService.deleteComment(postId, commentId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
