package balancetalk.talkpick.application;

import balancetalk.file.domain.FileType;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.repository.TalkPickRepository;
import balancetalk.vote.domain.TalkPickVote;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static balancetalk.bookmark.domain.BookmarkType.TALK_PICK;
import static balancetalk.global.exception.ErrorCode.NOT_FOUND_TALK_PICK;
import static balancetalk.talkpick.dto.TalkPickDto.*;

@Service
@RequiredArgsConstructor
public class TalkPickService {

    private final MemberRepository memberRepository;
    private final TalkPickRepository talkPickRepository;
    private final FileRepository fileRepository;

    @Transactional
    public void createTalkPick(CreateOrUpdateTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick savedTalkPick = talkPickRepository.save(request.toEntity(member));
        fileRepository.updateResourceIdAndTypeByStoredNames(savedTalkPick.getId(), FileType.TALK_PICK, request.getStoredNames());
    }

    @Transactional
    public TalkPickDetailResponse findById(Long talkPickId, GuestOrApiMember guestOrApiMember) {
        TalkPick talkPick = talkPickRepository.findById(talkPickId)
                .orElseThrow(() -> new BalanceTalkException(NOT_FOUND_TALK_PICK));
        talkPick.increaseViews();

        List<String> imgUrls = fileRepository.findImgUrlsByResourceIdAndFileType(talkPickId, FileType.TALK_PICK);
        List<String> imgStoredNames = fileRepository.findStoredNamesByResourceIdAndFileType(talkPickId, FileType.TALK_PICK);

        if (guestOrApiMember.isGuest()) {
            return TalkPickDetailResponse.from(talkPick, imgUrls, imgStoredNames, false, null);
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        boolean hasBookmarked = member.hasBookmarked(talkPickId, TALK_PICK);
        Optional<TalkPickVote> myVote = member.getVoteOnTalkPick(talkPick);

        if (myVote.isEmpty()) {
            return TalkPickDetailResponse.from(talkPick, imgUrls, imgStoredNames, hasBookmarked, null);
        }

        return TalkPickDetailResponse.from(talkPick, imgUrls, imgStoredNames, hasBookmarked, myVote.get().getVoteOption());
    }

    public Page<TalkPickResponse> findPaged(Pageable pageable) {
        return talkPickRepository.findPagedTalkPicks(pageable);
    }

    public List<TalkPickResponse> findBestTalkPicks() {
        return talkPickRepository.findBestTalkPicks();
    }

    @Transactional
    public void updateTalkPick(Long talkPickId, CreateOrUpdateTalkPickRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick talkPick = member.getTalkPickById(talkPickId);
        talkPick.update(request.toEntity(member));
        fileRepository.updateResourceIdAndTypeByStoredNames(talkPickId, FileType.TALK_PICK, request.getStoredNames());
    }

    @Transactional
    public void deleteTalkPick(Long talkPickId, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TalkPick talkPick = member.getTalkPickById(talkPickId);
        talkPickRepository.delete(talkPick);
    }
}
