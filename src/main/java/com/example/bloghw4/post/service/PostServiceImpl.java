package com.example.bloghw4.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bloghw4.global.BaseResponseDTO;
import com.example.bloghw4.jwtutil.UserDetails;
import com.example.bloghw4.post.dto.PostRequestDTO;
import com.example.bloghw4.post.dto.PostResponseDTO;
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
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    // 게시글 생성
    @Transactional
    @Override
    public PostResponseDTO createPost(PostRequestDTO postRequestDTO, UserDetails userDetails) {
        User user = getUserByUsername(userDetails.getUsername());
        Post post = Post.builder()
            .title(postRequestDTO.getTitle())
            .contents(postRequestDTO.getContents())
            .user(user)
            .build();
        Post savedPost = postRepository.save(post);

        PostResponseDTO response = new PostResponseDTO(savedPost);
        return response;
    }


    // 게시글 전체 조회
    @Transactional(readOnly = true)
    @Override
    public List<PostResponseDTO> getPosts() {
        List<Post> posts = postRepository.findAllByOrderByCreatedDateDesc();

        List<PostResponseDTO> response = posts.stream()
            .map(PostResponseDTO::new)
            .collect(Collectors.toList());
        return response;
    }


    // 게시글 지정 조회
    @Transactional(readOnly = true)
    @Override
    public PostResponseDTO getPost(Long postId) {
        Post post = getPostById(postId);

        PostResponseDTO response = new PostResponseDTO(post);
        return response;
    }


    // 게시글 수정
    @Transactional
    @Override
    public PostResponseDTO modifyPost(Long postId, PostRequestDTO postRequestDTO, UserDetails userDetails) {
        User user = getUserByUsername(userDetails.getUsername());

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));

        if(!hasRole(userDetails,user,post)) {
            throw new PermissionException("작성자만 삭제/수정할 수 있습니다.");
        }

        post.modifyPost(postRequestDTO.getTitle(), postRequestDTO.getContents());
        PostResponseDTO response = new PostResponseDTO(post);
        return response;
    }


    // 게시글 삭제
    @Transactional
    @Override
    public BaseResponseDTO deletePost(Long postId, UserDetails userDetails) {
        User user = getUserByUsername(userDetails.getUsername());

        Post post = getPostById(postId);

        if(!hasRole(userDetails,user,post)) {
            throw new PermissionException("작성자만 삭제/수정할 수 있습니다.");
        }

        postRepository.delete(post);
        return new BaseResponseDTO("게시글 삭제 성공", 200);
    }

    private boolean hasRole(UserDetails userDetails, User user, Post post) {
        return userDetails.getUserRole().equals(UserRole.ADMIN) ||
            post.getUser().getUsername().equals(user.getUsername());
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("회원을 찾을 수 없습니다."));
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
    }
}
