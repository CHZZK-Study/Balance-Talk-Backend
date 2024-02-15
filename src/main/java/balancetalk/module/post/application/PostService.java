package balancetalk.module.post.application;

import static balancetalk.global.exception.ErrorCode.*;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostLike;
import balancetalk.module.post.domain.PostLikeRepository;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.post.domain.PostTag;
import balancetalk.module.post.dto.PostRequestDto;
import balancetalk.module.post.dto.PostResponseDto;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import balancetalk.module.post.dto.PostResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;

    public Post save(final PostRequestDto postRequestDto) {

        Member member = memberRepository.findById(postRequestDto.getMemberId())
                .orElseThrow();

        Post postEntity = postRequestDto.toEntity(member);

        List<BalanceOption> options = postEntity.getOptions();
        for (BalanceOption option : options) {
            option.addPost(postEntity);
        }

        List<PostTag> postTags = postEntity.getPostTags();
        for (PostTag postTag : postTags) {
            postTag.addPost(postEntity);
        }
        return postRepository.save(postEntity);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> findAll() {
        // todo: 검색, 정렬, 마감 기능 추가
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(PostResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponseDto findById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("해당 post를 찾을 수 없습니다."));
        return PostResponseDto.fromEntity(post);
    }

    public void deleteById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글 삭제 실패"));
        // todo: 에러 추가
        postRepository.deleteById(postId);
    }

    public Long likePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));

        if (postLikeRepository.existsByMemberAndPost(member, post)) {
            throw new BalanceTalkException(ALREADY_LIKE_POST);
        }

        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();
        postLikeRepository.save(postLike);

        return post.getId();
    }
}
