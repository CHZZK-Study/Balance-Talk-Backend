package balancetalk.module.post.application;

import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.Bookmark;
import balancetalk.module.post.domain.BookmarkRepository;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.post.dto.BookmarkRequestDto;
import balancetalk.module.post.dto.BookmarkResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public Bookmark save(final BookmarkRequestDto bookmarkRequestDto, Long postId) {
        Member member = memberRepository.findById(bookmarkRequestDto.getMemberId())
                .orElseThrow();
        Post post = postRepository.findById(postId)
                .orElseThrow();
        Bookmark bookmark = bookmarkRequestDto.toEntity(member, post);

        return bookmarkRepository.save(bookmark);
    }

    @Transactional(readOnly = true) // TODO: Spring Security 도입 후 현재 인증된 사용자 정보 기반으로 조회하게 변경 필요
    public List<BookmarkResponseDto> findAllByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow();
        List<Bookmark> bookmarks = bookmarkRepository.findByMember(member);

        return bookmarks.stream()
                .map(BookmarkResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long bookmarkId) {
        bookmarkRepository.deleteById(bookmarkId);
    }
}
