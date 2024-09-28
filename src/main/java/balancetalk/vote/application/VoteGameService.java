package balancetalk.vote.application;

import static balancetalk.global.notification.domain.NotificationMessage.GAME_VOTE;
import static balancetalk.global.notification.domain.NotificationMessage.GAME_VOTE_100;
import static balancetalk.global.notification.domain.NotificationMessage.GAME_VOTE_1000;
import static balancetalk.global.notification.domain.NotificationStandard.FIRST_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.FOURTH_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.SECOND_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationStandard.THIRD_STANDARD_OF_NOTIFICATION;
import static balancetalk.global.notification.domain.NotificationTitleCategory.WRITTEN_GAME;
import static balancetalk.vote.domain.VoteOption.A;
import static balancetalk.vote.domain.VoteOption.B;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameOption;
import balancetalk.game.domain.GameReader;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.global.notification.application.NotificationService;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.vote.domain.GameVote;
import balancetalk.vote.domain.VoteRepository;
import balancetalk.vote.dto.VoteGameDto.VoteRequest;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteGameService {

    private final GameReader gameReader;
    private final VoteRepository voteRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    public void createVote(Long gameId, VoteRequest request, GuestOrApiMember guestOrApiMember) {
        Game game = gameReader.findGameById(gameId);
        GameOption gameOption = getGameOption(game, request);

        if (guestOrApiMember.isGuest()) {
            voteRepository.save(request.toEntity(null, gameOption));
            return;
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        if (member.hasVotedGame(game)) {
            throw new BalanceTalkException(ErrorCode.ALREADY_VOTE);
        }
        voteRepository.save(request.toEntity(member, gameOption));
        sendVoteGameNotification(game);
    }

    public void updateVote(Long gameId, VoteRequest request, ApiMember apiMember) {
        Game game = gameReader.findGameById(gameId);

        Member member = apiMember.toMember(memberRepository);

        Optional<GameVote> voteOnGame = member.getVoteOnGame(game);
        if (voteOnGame.isEmpty()) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE);
        }

        GameVote vote = voteOnGame.get();
        GameOption gameOption = getGameOption(game, request);

        vote.updateVoteOption(request.getVoteOption());
        vote.updateGameOption(gameOption);
    }

    private GameOption getGameOption(Game game, VoteRequest request) {
        return game.getGameOptions().stream()
                .filter(option -> option.getOptionType().equals(request.getVoteOption()))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE_OPTION));
    }

    public void deleteVote(Long gameId, ApiMember apiMember) {
        Game game = gameReader.findGameById(gameId);

        Member member = apiMember.toMember(memberRepository);

        Optional<GameVote> voteOnGame = member.getVoteOnGame(game);
        if (voteOnGame.isEmpty()) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE);
        }
        voteRepository.delete(voteOnGame.get());
    }

    private void sendVoteGameNotification(Game game) {
        Member member = null; // TODO: GameSet으로 변경됨에 따라 수정 필요
        long votedCount = game.getVoteCount(A) + game.getVoteCount(B);
        String voteCountKey = "VOTE_" + votedCount;
        Map<String, Boolean> notificationHistory = game.getNotificationHistory();
        String category = WRITTEN_GAME.getCategory();

        boolean isMilestoneVoted = (votedCount == FIRST_STANDARD_OF_NOTIFICATION.getCount() ||
                votedCount == SECOND_STANDARD_OF_NOTIFICATION.getCount() ||
                votedCount == THIRD_STANDARD_OF_NOTIFICATION.getCount() ||
                (votedCount > THIRD_STANDARD_OF_NOTIFICATION.getCount() &&
                        votedCount % THIRD_STANDARD_OF_NOTIFICATION.getCount() == 0) ||
                (votedCount > FOURTH_STANDARD_OF_NOTIFICATION.getCount() &&
                        votedCount % FOURTH_STANDARD_OF_NOTIFICATION.getCount() == 0));

        // 투표 개수가 10, 50, 100*n개, 1000*n개 일 때 알림
        if (isMilestoneVoted && !notificationHistory.getOrDefault(voteCountKey, false)) {
            notificationService.sendGameNotification(member, game, category, GAME_VOTE.format(votedCount));
            // 투표 개수가 100개일 때 배찌 획득 알림
            if (votedCount == THIRD_STANDARD_OF_NOTIFICATION.getCount()) {
                notificationService.sendGameNotification(member, game, category, GAME_VOTE_100.getMessage());
            }
            // 투표 개수가 1000개일 때 배찌 획득 알림
            else if (votedCount == FOURTH_STANDARD_OF_NOTIFICATION.getCount()) {
                notificationService.sendGameNotification(member, game, category, GAME_VOTE_1000.getMessage());
            }
            notificationHistory.put(voteCountKey, true);
            game.setNotificationHistory(notificationHistory);
        }
    }
}
