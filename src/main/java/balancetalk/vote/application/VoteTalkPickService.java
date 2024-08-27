package balancetalk.vote.application;

import static balancetalk.global.notification.domain.NotificationMessage.TALK_PICK_VOTE;
import static balancetalk.global.notification.domain.NotificationMessage.TALK_PICK_VOTE_100;
import static balancetalk.global.notification.domain.NotificationMessage.TALK_PICK_VOTE_1000;
import static balancetalk.global.notification.domain.NotificationStandard.FIRST_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.FOURTH_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.SECOND_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.THIRD_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationTitleCategory.WRITTEN_TALK_PICK;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.notification.application.NotificationService;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.talkpick.domain.TalkPick;
import balancetalk.talkpick.domain.TalkPickReader;
import balancetalk.vote.domain.Vote;
import balancetalk.vote.domain.VoteRepository;
import balancetalk.vote.dto.VoteTalkPickDto.VoteRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteTalkPickService {

    private final TalkPickReader talkPickReader;
    private final VoteRepository voteRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    @Transactional
    public void createVote(long talkPickId, VoteRequest request, GuestOrApiMember guestOrApiMember) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);

        if (guestOrApiMember.isGuest()) {
            voteRepository.save(request.toEntity(null, talkPick));
            return;
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        if (member.hasVotedTalkPick(talkPick)) {
            throw new BalanceTalkException(ErrorCode.ALREADY_VOTE);
        }

        voteRepository.save(request.toEntity(member, talkPick));
        sendVoteTalkPickNotification(talkPick);
    }

    @Transactional
    public void updateVote(long talkPickId, VoteRequest request, ApiMember apiMember) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);
        Member member = apiMember.toMember(memberRepository);

        Optional<Vote> vote = member.getVoteOnTalkPick(talkPick);
        if (vote.isEmpty()) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE);
        }

        vote.get().updateVoteOption(request.getVoteOption());
    }

    @Transactional
    public void deleteVote(long talkPickId, ApiMember apiMember) {
        TalkPick talkPick = talkPickReader.readById(talkPickId);
        Member member = apiMember.toMember(memberRepository);

        Optional<Vote> vote = member.getVoteOnTalkPick(talkPick);
        if (vote.isEmpty()) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE);
        }

        voteRepository.delete(vote.get());
    }

    private void sendVoteTalkPickNotification(TalkPick talkPick) {
        Member member = talkPick.getMember();
        long votedCount = talkPick.getVotes().size();
        String voteCountKey = "VOTE_" + votedCount;
        Map<String, Boolean> notificationHistory = talkPick.getNotificationHistory();
        String category = WRITTEN_TALK_PICK.getCategory();

        boolean isMilestoneBookmarked = (votedCount == FIRST_STANDARD_OF_NOTIFICATION.getCount() ||
                votedCount == SECOND_STANDARD_OF_NOTIFICATION.getCount() ||
                votedCount == THIRD_STANDARD_OF_NOTIFICATION.getCount() ||
                (votedCount > THIRD_STANDARD_OF_NOTIFICATION.getCount() &&
                        votedCount % THIRD_STANDARD_OF_NOTIFICATION.getCount() == 0) ||
                (votedCount > FOURTH_STANDARD_OF_NOTIFICATION.getCount() &&
                        votedCount % FOURTH_STANDARD_OF_NOTIFICATION.getCount() == 0));

        // 투표 개수가 10, 50, 100*n개, 1000*n개 일 때 알림
        if (isMilestoneBookmarked && !notificationHistory.getOrDefault(voteCountKey, false)) {
            notificationService.sendTalkPickNotification(member, talkPick, category, TALK_PICK_VOTE.format(votedCount));
            // 투표 개수가 100개일 때 배찌 획득 알림
            if (votedCount == THIRD_STANDARD_OF_NOTIFICATION.getCount()) {
                notificationService.sendTalkPickNotification(member, talkPick, category, TALK_PICK_VOTE_100.getMessage());
            }
            // 투표 개수가 1000개일 때 배찌 획득 알림
            else if (votedCount == FOURTH_STANDARD_OF_NOTIFICATION.getCount()) {
                notificationService.sendTalkPickNotification(member, talkPick, category, TALK_PICK_VOTE_1000.getMessage());
            }
            notificationHistory.put(voteCountKey, true);
            talkPick.setNotificationHistory(notificationHistory);
        }
    }
}
