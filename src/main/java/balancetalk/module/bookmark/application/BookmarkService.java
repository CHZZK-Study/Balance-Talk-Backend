package balancetalk.module.bookmark.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.bookmark.domain.Bookmark;
import balancetalk.module.bookmark.domain.BookmarkRepository;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.bookmark.dto.BookmarkRequest;
import balancetalk.module.bookmark.dto.BookmarkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static balancetalk.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public Bookmark save(final BookmarkRequest bookmarkRequestDto, Long postId) {
        Member member = memberRepository.findById(bookmarkRequestDto.getMemberId())
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
        if (member.hasBookmarked(post)) {
            throw new BalanceTalkException(ALREADY_BOOKMARK);
        }
        Bookmark bookmark = bookmarkRequestDto.toEntity(member, post);

        return bookmarkRepository.save(bookmark);
    }

    @Transactional(readOnly = true) // TODO: Spring Security 도입 후 현재 인증된 사용자 정보 기반으로 조회하게 변경 필요
    public List<BookmarkResponse> findAllByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
        List<Bookmark> bookmarks = bookmarkRepository.findByMember(member);

        return bookmarks.stream()
                .map(BookmarkResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long memberId, Long bookmarkId) { // TODO: Spring Security 도입 후 현재 인증된 사용자 정보 기반으로 삭제하게 변경 필요
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
        bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BOOKMARK));

        bookmarkRepository.deleteById(bookmarkId);
    }
}
