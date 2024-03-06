package balancetalk.module.bookmark.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.module.bookmark.domain.Bookmark;
import balancetalk.module.bookmark.domain.BookmarkRepository;
import balancetalk.module.bookmark.dto.BookmarkResponse;
import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Bookmark createBookmark(Long postId) {
        Member member = getCurrentMember();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_POST));
        if (member.hasBookmarked(post)) {
            throw new BalanceTalkException(ALREADY_BOOKMARK);
        }
        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .post(post)
                .build();

        return bookmarkRepository.save(bookmark);
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponse> findAllByMember() {
        Member member = getCurrentMember();
        List<Bookmark> bookmarks = bookmarkRepository.findByMember(member);

        return bookmarks.stream()
                .map(BookmarkResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteByPostId(Long postId) {
        Member member = getCurrentMember();
        Bookmark bookmark = bookmarkRepository.findByMemberAndPostId(member, postId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_BOOKMARK));

        if (!bookmark.getMember().equals(member)) {
            throw new BalanceTalkException(FORBIDDEN_BOOKMARK_DELETE);
        }

        bookmarkRepository.delete(bookmark);
    }

    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_MEMBER));
    }
}
