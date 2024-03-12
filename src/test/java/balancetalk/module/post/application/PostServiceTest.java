package balancetalk.module.post.application;

import balancetalk.global.redis.application.RedisService;
import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.*;
import balancetalk.module.post.dto.BalanceOptionDto;
import balancetalk.module.post.dto.PostRequest;
import balancetalk.module.post.dto.PostResponse;
import balancetalk.module.post.dto.PostTagDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    PostLikeRepository postLikeRepository;

    @Mock
    FileRepository fileRepository;

    @Mock
    RedisService redisService;

    @InjectMocks
    PostService postService;

    private String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNDVAbmF2ZXIuY29tIiwiaWF0IjoxNzA5NDc1NTE4LCJleHAiOjE3MDk1MTg3MTh9.ZZXuN4OWM2HZjWOx7Pupl5NkRtjvd4qnK_txGdRy7G5_GdKgnyF3JfiUsenQgxsi1Y_-7C0dA85xabot2m1cag";

    Member member = Member.builder()
            .id(1L)
            .email("member@gmail.com")
            .build();

    File file = File.builder()
            .storedName("e90a6177-89a1-45b3-91d3-cb39e9bec407_미어캣.jpg")
            .build();
    BalanceOption balanceOption = BalanceOption.builder()
            .title("option1")
            .description("description1")
            .file(file)
            .build();

    @BeforeEach
    void setUp() {
        // SecurityContext에 인증된 사용자 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


    }

    @Test
    @DisplayName("게시글 작성 성공")
    void postSaveSuccess() {
        // given
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(redisService.getValues(member.getEmail())).thenReturn(accessToken);

        List<File> images = new ArrayList<>();
        images.add(file);



    }
//
//    @Test
//    @DisplayName("모든 게시글 조회")
//    void readAllPosts_Success() {
//        // given
//        List<Post> posts = List.of(
//                Post.builder()
//                    .id(1L)
//                    .options(Collections.emptyList())
//                    .postTags(Collections.emptyList())
//                    .build(),
//                Post.builder()
//                    .id(2L)
//                    .options(Collections.emptyList())
//                    .postTags(Collections.emptyList())
//                    .build()
//        );
//
//        when(postRepository.findAll()).thenReturn(posts);
//
//        // when
//        List<PostResponse> result = postService.findAll();
//
//        // then
//        assertEquals(result.get(0).getId(), 1L);
//        assertEquals(result.get(1).getId(), 2L);
//    }
//
//    @Test
//    @DisplayName("게시글 단건 조회")
//    void readSinglePost_Success()  {
//        // given
//        Post post = Post.builder()
//                .id(1L)
//                .title("게시글_단건_조회_테스트")
//                .options(Collections.emptyList())
//                .postTags(Collections.emptyList())
//                .build();
//
//        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
//
//        // when
//        PostResponse result = postService.findById(post.getId());
//
//        // then
//        assertEquals(post.getId() , result.getId());
//        assertEquals(post.getTitle() , result.getTitle());
//    }
//
//    @Test
//    @DisplayName("게시글 id로 게시글 삭제")
//    void deletePostById_Success() {
//        // given
//        Post post = Post.builder()
//                .id(1L)
//                .title("게시글_삭제_테스트")
//                .options(Collections.emptyList())
//                .postTags(Collections.emptyList())
//                .build();
//
//        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
//
//        PostResponse result = postService.findById(post.getId());
//
//        // when
//        postService.deleteById(result.getId());
//
//        // then
//        assertFalse(postService.findAll().contains(result));
//    }
//
//    @Test
//    @DisplayName("게시글 id가 다르면 게시글 삭제 실패")
//    void deletePostById_Fail() {
//        // given
//        Post post = Post.builder()
//                .id(1L)
//                .title("게시글_삭제_테스트")
//                .options(Collections.emptyList())
//                .postTags(Collections.emptyList())
//                .build();
//
//        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
//
//        PostResponse result = postService.findById(post.getId());
//
//        // when
//        assertThatThrownBy(() -> postService.deleteById(2L))
//                .isInstanceOf(BalanceTalkException.class);
//    }
//
//    public Member createMember(Long id) {
//        return Member.builder().id(id).build();
//    }
//
//    public File createFile() {
//        return File.builder().originalName("파일1").path("../").type(FileType.JPEG).size(236L).build();
//    }
//
//    public PostTag createPostTag() {
//        return PostTag.builder().tag(Tag.builder().name("태그1").build()).build();
//    }
//
//    public BalanceOption createBalanceOption() {
//        return BalanceOption.builder().title("밸런스_선택지").description("설명").file(createFile()).build();
//    }
//
//    public Post createPost(Long id) {
//        return Post.builder()
//                .id(id)
//                .member(createMember(id))
//                .title("제목1")
//                .options(List.of(createBalanceOption()))
//                .postTags(List.of(createPostTag()))
//                .build();
//    }
//
//    @Test
//    @DisplayName("사용자가 특정 게시글에 추천을 누르면 해당 게시글 id가 반환된다.")
//    void createPostLike_Success() {
//        // given
//        Post post = Post.builder()
//                .id(1L)
//                .build();
//        Member member = Member.builder()
//                .id(1L)
//                .build();
//
//        when(postRepository.findById(any())).thenReturn(Optional.of(post));
//        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
//
//        // when
//        Long likedPostId = postService.likePost(post.getId(), member.getId());
//
//        // then
//        assertThat(likedPostId).isEqualTo(post.getId());
//    }
//
//    @Test
//    @DisplayName("게시글 추천 시 해당 게시글이 존재하지 않는 경우 예외 발생")
//    void createPostLike_Fail_ByNotFoundPost() {
//        // given
//        Member member = Member.builder()
//                .id(1L)
//                .build();
//
//        when(postRepository.findById(any())).thenThrow(new BalanceTalkException(NOT_FOUND_POST));
//
//        // when, then
//        assertThatThrownBy(() -> postService.likePost(1L, member.getId()))
//                .isInstanceOf(BalanceTalkException.class)
//                .hasMessageContaining(NOT_FOUND_POST.getMessage());
//    }
//
//    @Test
//    @DisplayName("게시글 중복 추천 시 예외 발생")
//    void createPostLike_Fail_ByAlreadyLikePost() {
//        // given
//        Post post = Post.builder()
//                .id(1L)
//                .build();
//        Member member = Member.builder()
//                .id(1L)
//                .build();
//
//        when(postRepository.findById(any())).thenReturn(Optional.of(post));
//        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
//        when(postLikeRepository.existsByMemberAndPost(member, post))
//                .thenThrow(new BalanceTalkException(ALREADY_LIKE_POST));
//
//        // when, then
//        assertThatThrownBy(() -> postService.likePost(post.getId(), member.getId()))
//                .isInstanceOf(BalanceTalkException.class)
//                .hasMessageContaining(ALREADY_LIKE_POST.getMessage());
//    }
}