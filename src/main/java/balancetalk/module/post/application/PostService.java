package balancetalk.module.post.application;

import static balancetalk.global.exception.ErrorCode.*;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileRepository;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostLike;
import balancetalk.module.post.domain.PostLikeRepository;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.post.domain.PostTag;
import balancetalk.module.post.dto.BalanceOptionDto;
import balancetalk.module.post.dto.PostRequestDto;
import balancetalk.module.post.dto.PostResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final FileRepository fileRepository;

    public PostResponseDto save(final PostRequestDto postRequestDto) {
        Member member = getMember(postRequestDto);
        List<File> images = getImages(postRequestDto);
        Post post = postRequestDto.toEntity(member, images);

        List<BalanceOption> options = post.getOptions();
        for (BalanceOption option : options) {
            option.addPost(post);
        }
        List<PostTag> postTags = post.getPostTags();
        for (PostTag postTag : postTags) {
            postTag.addPost(post);
        }

        return PostResponseDto.fromEntity(postRepository.save(post), member);
    }

    private Member getMember(PostRequestDto postRequestDto) {
        return memberRepository.findById(postRequestDto.getMemberId())
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
    }

    private List<File> getImages(PostRequestDto postRequestDto) {
        List<BalanceOptionDto> balanceOptions = postRequestDto.getBalanceOptions();
        return balanceOptions.stream()
                .map(optionDto -> fileRepository.findByStoredName(optionDto.getStoredFileName())
                        .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_FILE)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> findAll(Long memberId) {
        // TODO: 검색, 정렬, 마감 기능 추가
        List<Post> posts = postRepository.findAll();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
        return posts.stream()
                .map(post -> PostResponseDto.fromEntity(post, member))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponseDto findById(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
        return PostResponseDto.fromEntity(post, member);
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
