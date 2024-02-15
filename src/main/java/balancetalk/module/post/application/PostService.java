package balancetalk.module.post.application;

import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.BalanceOption;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.post.domain.PostTag;
import balancetalk.module.post.dto.PostRequestDto;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import balancetalk.module.post.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
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

    @Transactional
    public void deleteById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글 삭제 실패"));
        // todo: 에러 추가
        postRepository.deleteById(postId);
    }
}
