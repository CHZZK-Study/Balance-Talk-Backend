package balancetalk.talkpick.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import balancetalk.vote.domain.Vote;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static balancetalk.bookmark.domain.BookmarkType.TALK_PICK;
import static balancetalk.talkpick.dto.TalkPickDto.*;

@Service
@RequiredArgsConstructor
public class TalkPickService {

    private final MemberRepository memberRepository;
    private final TalkPickRepository talkPickRepository;

    @Transactional
    public void createTalkPick(CreateTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        talkPickRepository.save(request.toEntity(member));
    }

    @Transactional
    public TalkPickDetailResponse findById(Long talkPickId, GuestOrApiMember guestOrApiMember) {
        TalkPick talkPick = talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_TALK_PICK));
        talkPick.increaseViews();

        if (guestOrApiMember.isGuest()) {
            return TalkPickDetailResponse.from(talkPick, false, null);
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        boolean hasBookmarked = member.hasBookmarked(talkPickId, TALK_PICK);
        Optional<Vote> myVote = member.getVoteOnTalkPick(talkPick);

        if (myVote.isEmpty()) {
            return TalkPickDetailResponse.from(talkPick, hasBookmarked, null);
        }

        return TalkPickDetailResponse.from(talkPick, hasBookmarked, myVote.get().getVoteOption());
    }

    public Page<TalkPickResponse> findPaged(Pageable pageable) {
        return talkPickRepository.findPagedTalkPicks(pageable);
    }

    public List<TalkPickResponse> findBestTalkPicks() {
        return talkPickRepository.findBestTalkPicks();
    }

    @Transactional
    public void updateTalkPick(Long talkPickId, UpdateTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick talkPick = member.getTalkPickById(talkPickId);
        talkPick.edit(request.getTitle(), request.getContent(), request.getOptionA(), request.getOptionB());
    }

    @Transactional
    public void deleteTalkPick(Long talkPickId, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick talkPick = member.getTalkPickById(talkPickId);
        talkPickRepository.delete(talkPick);
    }
}
