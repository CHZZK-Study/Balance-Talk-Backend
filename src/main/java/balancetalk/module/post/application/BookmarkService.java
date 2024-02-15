package balancetalk.module.post.application;

import balancetalk.module.member.domain.Member;
import balancetalk.module.member.domain.MemberRepository;
import balancetalk.module.post.domain.Bookmark;
import balancetalk.module.post.domain.BookmarkRepository;
import balancetalk.module.post.domain.Post;
import balancetalk.module.post.domain.PostRepository;
import balancetalk.module.post.dto.BookmarkRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
