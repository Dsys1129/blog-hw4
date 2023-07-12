package com.example.bloghw4.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bloghw4.comment.dto.CommentRequestDTO;
import com.example.bloghw4.comment.dto.CommentResponseDTO;
import com.example.bloghw4.comment.entity.Comment;
import com.example.bloghw4.comment.exception.CommentNotFoundException;
import com.example.bloghw4.comment.repository.CommentRepository;
import com.example.bloghw4.global.BaseResponseDTO;
import com.example.bloghw4.jwtutil.UserDetails;
import com.example.bloghw4.post.entity.Post;
import com.example.bloghw4.post.exception.PermissionException;
import com.example.bloghw4.post.exception.PostNotFoundException;
import com.example.bloghw4.post.repository.PostRepository;
import com.example.bloghw4.user.entity.User;
import com.example.bloghw4.user.entity.UserRole;
import com.example.bloghw4.user.exception.UserNotFoundException;
import com.example.bloghw4.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public CommentResponseDTO createComment(Long postId, CommentRequestDTO commentRequestDTO, UserDetails userDetails) {
        User user = getUserByUsername(userDetails.getUsername());
        Post post = getPostById(postId);
        Comment comment = Comment.builder()
            .post(post)
            .user(user)
            .contents(commentRequestDTO.getContents())
            .build();
        commentRepository.save(comment);

        CommentResponseDTO response = new CommentResponseDTO(comment);
        return response;
    }

    @Transactional
    @Override
    public CommentResponseDTO modifyComment(Long postId, Long commentId, CommentRequestDTO commentRequestDTO, UserDetails userDetails) {
        User user = getUserByUsername(userDetails.getUsername());
        Comment comment =  getCommentById(commentId);

        if (!hasRole(userDetails, user, comment)) {
            throw new PermissionException("작성자만 삭제/수정할 수 있습니다.");
        }

        comment.modifyComment(commentRequestDTO.getContents());
        CommentResponseDTO response = new CommentResponseDTO(comment);
        return response;
    }

    @Transactional
    @Override
    public BaseResponseDTO deleteComment(Long postId, Long commentId, UserDetails userDetails) {
        User user = getUserByUsername(userDetails.getUsername());

        Comment comment = getCommentById(commentId);

        if (!hasRole(userDetails, user, comment)) {
            throw new PermissionException("작성자만 삭제/수정할 수 있습니다.");
        }
        commentRepository.delete(comment);

        BaseResponseDTO response = new BaseResponseDTO("댓글 삭제 성공", 200);
        return response;
    }

    private boolean hasRole(UserDetails userDetails, User user, Comment comment) {
        return userDetails.getUserRole() .equals(UserRole.ADMIN) ||
            comment.getUser().getUsername().equals(user.getUsername());
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("회원을 찾을 수 없습니다."));
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
    }
}
