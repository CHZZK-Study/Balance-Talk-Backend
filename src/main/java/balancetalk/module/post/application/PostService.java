package balancetalk.module.post.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.redis.application.RedisService;
import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.*;
import balancetalk.module.post.dto.BalanceOptionDto;
import balancetalk.module.post.dto.PostRequest;
import balancetalk.module.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static balancetalk.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final FileRepository fileRepository;
    private final RedisService redisService;

    public PostResponse save(final PostRequest request) {
        Member member = getMember(request);
        if (redisService.getValues(member.getEmail()) == null) {
            throw new BalanceTalkException(FORBIDDEN_POST_CREATE);
        }
        List<File> images = getImages(request);
        Post post = request.toEntity(member, images);

        List<BalanceOption> options = post.getOptions();
        for (BalanceOption option : options) {
            option.addPost(post);
        }
        List<PostTag> postTags = post.getPostTags();
        for (PostTag postTag : postTags) {
            postTag.addPost(post);
        }

        return PostResponse.fromEntity(postRepository.save(post), member);
    }

    private Member getMember(PostRequest postRequestDto) {
        return memberRepository.findById(postRequestDto.getMemberId())
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
    }

    private List<File> getImages(PostRequest postRequestDto) {
        List<BalanceOptionDto> balanceOptions = postRequestDto.getBalanceOptions();
        return balanceOptions.stream()
                .filter(optionDto -> optionDto.getStoredFileName() != null && !optionDto.getStoredFileName().isEmpty())
                .map(optionDto -> fileRepository.findByStoredName(optionDto.getStoredFileName())
                        .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_FILE)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAll(Long memberId) {
        // TODO: 검색, 정렬, 마감 기능 추가
        List<Post> posts = postRepository.findAll();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
        return posts.stream()
                .map(post -> PostResponse.fromEntity(post, member))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse findById(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));

        return PostResponse.fromEntity(post, member);
    }

    public void deleteById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
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

    public void cancelLikePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));

        postLikeRepository.deleteByMemberAndPost(member, post);
    }
}
