package com.example.bloghw4.comment.service;



import com.example.bloghw4.comment.dto.CommentRequestDTO;
import com.example.bloghw4.comment.dto.CommentResponseDTO;
import com.example.bloghw4.global.BaseResponseDTO;
import com.example.bloghw4.jwtutil.UserDetails;

public interface CommentService {

    CommentResponseDTO createComment(Long postId, CommentRequestDTO commentRequestDTO, UserDetails userDetails);

    CommentResponseDTO modifyComment(Long postId, Long commentId, CommentRequestDTO commentRequestDTO, UserDetails userDetails);

    BaseResponseDTO deleteComment(Long postId, Long commentId, UserDetails userDetails);

}
