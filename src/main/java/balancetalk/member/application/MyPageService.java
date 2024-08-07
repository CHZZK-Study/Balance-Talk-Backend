package balancetalk.member.application;

import balancetalk.bookmark.domain.Bookmark;
import balancetalk.bookmark.domain.BookmarkRepository;
import balancetalk.bookmark.domain.BookmarkType;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import balancetalk.talkpick.dto.TalkPickDto.TalkPickMyPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final TalkPickRepository talkPickRepository;
    private final BookmarkRepository bookmarkRepository;

    public Page<TalkPickMyPageResponse> findAllBookmarkedTalkPicks(ApiMember apiMember, Pageable pageable) {
        Member member = apiMember.toMember(memberRepository);
        List<Bookmark> bookmarks = bookmarkRepository.findAllByMemberId(member.getId(), BookmarkType.TALK_PICK);

        List<Long> talkPickIds = bookmarks.stream()
                .map(Bookmark::getResourceId)
                .collect(Collectors.toList());

        if (talkPickIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<TalkPick> talkPicks = talkPickRepository.findByIdIn(talkPickIds);
        Map<Long, TalkPick> talkPickMap = talkPicks.stream()
                .collect(Collectors.toMap(TalkPick::getId, Function.identity()));

        List<TalkPickMyPageResponse> responses = talkPickIds.stream()
                .map(talkPickMap::get)
                .filter(Objects::nonNull)
                .map(TalkPickMyPageResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, responses.size());
    }
}