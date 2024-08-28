package balancetalk.vote.application;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameOption;
import balancetalk.game.domain.GameReader;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.vote.domain.GameVote;
import balancetalk.vote.domain.VoteRepository;
import balancetalk.vote.dto.VoteGameDto.VoteRequest;
import jakarta.transaction.Transactional;
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

    public void createVote(Long gameId, VoteRequest request, GuestOrApiMember guestOrApiMember) {
        Game game = gameReader.readById(gameId);
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
    }

    public void updateVote(Long gameId, VoteRequest request, ApiMember apiMember) {
        Game game = gameReader.readById(gameId);

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
        Game game = gameReader.readById(gameId);

        Member member = apiMember.toMember(memberRepository);

        Optional<GameVote> voteOnGame = member.getVoteOnGame(game);
        if (voteOnGame.isEmpty()) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE);
        }
        voteRepository.delete(voteOnGame.get());
    }
}
