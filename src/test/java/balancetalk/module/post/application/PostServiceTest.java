package balancetalk.module.post.application;

import balancetalk.module.ViewStatus;
import balancetalk.module.file.domain.FileType;
import balancetalk.module.file.dto.FileDto;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostCategory;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.post.dto.BalanceOptionDto;
import balancetalk.module.post.dto.PostRequestDto;
import balancetalk.module.post.dto.PostTagDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@RequiredArgsConstructor
@SpringBootTest
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @AfterEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 작성 - 성공")
    @Transactional
    void test() {
        // given
        FileDto fileDto = FileDto.builder()
                .uploadName("파일1")
                .path("../")
                .type(FileType.JPEG)
                .size(236L)
                .build();

        List<PostTagDto> postTagDto = List.of(
                PostTagDto.builder()
                        .tagName("태그1")
                        .build(),
                PostTagDto.builder()
                        .tagName("태그2")
                        .build());

        List<BalanceOptionDto> balanceOptionDto = List.of(
                BalanceOptionDto.builder()
                        .title("제목1")
                        .description("섦명 내용")
                        .file(fileDto)
                        .build(),
                BalanceOptionDto.builder()
                        .title("제목1")
                        .description("섦명 내용")
                        .file(fileDto)
                        .build());

        PostRequestDto postRequestDto = PostRequestDto.builder()
                .id(1L)
                .title("게시글_성공_테스트")
                .deadline(LocalDateTime.parse("2024-12-15T10:00:00"))
                .views(0L)
                .viewStatus(ViewStatus.NORMAL)
                .category(PostCategory.DISCUSSION)
                .balanceOptions(balanceOptionDto)
                .tags(postTagDto)
                .build();

        Post savedPost = postRequestDto.toEntity();
        when(postRepository.save(any())).thenReturn(savedPost);
        when(postRepository.findById(savedPost.getId())).thenReturn(Optional.of(savedPost));

        // when
        Post result = postService.save(postRequestDto);

        // then
        Assertions.assertThat(savedPost.getId()).isEqualTo(result.getId());
    }
}