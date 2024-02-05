package balancetalk.module.post.application;

import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.post.dto.BalanceOptionDto;
import balancetalk.module.post.dto.PostRequestDto;
import balancetalk.module.post.dto.PostTagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RequiredArgsConstructor
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 작성 - 성공")
    void test() {
        // given
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .id(1L)
                .title("게시글_성공_테스트")
                .deadline(LocalDateTime.parse("2024-12-15T10:00:00"))
                .views(0L)
                .Category(PostCategory.DISCUSSION)
                .balanceOptions(List.of(
                        BalanceOptionDto.builder()
                                .title("test1")
                                .description("test1 description")
                                .build(),
                        BalanceOptionDto.builder()
                                .title("test2")
                                .description("test2 description")
                                .build()
                ))
                .tags(List.of(
                        PostTagDto.builder()
                                .tagName("태그1")
                                .build()
                ))
                .build();
        // when
        postService.save(postRequestDto);

        // then
        Post savedPost = postRepository.findById(postRequestDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        assertEquals(postRequestDto.getId(), savedPost.getId());
    }
}