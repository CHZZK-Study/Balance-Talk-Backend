package balancetalk.talkpick.application;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import balancetalk.talkpick.dto.TalkPickDto.TalkPickDetailResponse;
import balancetalk.vote.domain.Vote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static balancetalk.bookmark.domain.BookmarkType.TALK_PICK;
import static balancetalk.member.dto.MemberDto.TokenDto;

@Service
@RequiredArgsConstructor
public class TalkPickService {

    private final TalkPickReader talkPickReader;
    private final MemberRepository memberRepository;

    @Transactional
    public TalkPickDetailResponse findById(Long talkPickId, TokenDto tokenDto) {
        TalkPick talkPick = talkPickReader.readTalkPickById(talkPickId);
        talkPick.increaseViews();

        if (tokenDto == null) {
            return TalkPickDetailResponse.from(talkPick, false, null);
        }

        // TODO 회원 조회 로직 변경 예정
        Member member = memberRepository.findByEmail(tokenDto.getEmail())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MEMBER));

        boolean hasBookmarked = member.hasBookmarked(talkPickId, TALK_PICK);
        Optional<Vote> myVote = member.getVoteOnTalkPick(talkPick);

        if (myVote.isEmpty()) {
            return TalkPickDetailResponse.from(talkPick, hasBookmarked, null);
        }

        return TalkPickDetailResponse.from(talkPick, hasBookmarked, myVote.get().getVoteOption());
    }
}
