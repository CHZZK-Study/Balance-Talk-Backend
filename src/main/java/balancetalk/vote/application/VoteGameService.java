package balancetalk.vote.application;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameReader;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.vote.domain.Vote;
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

        if (guestOrApiMember.isGuest()) {
            voteRepository.save(request.toEntity(null, game));
            return;
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        if (member.hasVotedGame(game)) {
            throw new BalanceTalkException(ErrorCode.ALREADY_VOTE);
        }
        voteRepository.save(request.toEntity(member, game));
    }

    public void updateVote(Long gameId, VoteRequest request, ApiMember apiMember) {
        Game game = gameReader.readById(gameId);

        Member member = apiMember.toMember(memberRepository);

        Optional<Vote> voteOnGame = member.getVoteOnGame(game);
        if (voteOnGame.isEmpty()) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE);
        }

        voteOnGame.get().updateVoteOption(request.getVoteOption());
    }

    public void deleteVote(Long gameId, ApiMember apiMember) {
        Game game = gameReader.readById(gameId);

        Member member = apiMember.toMember(memberRepository);

        Optional<Vote> voteOnGame = member.getVoteOnGame(game);
        if (voteOnGame.isEmpty()) {
            throw new BalanceTalkException(ErrorCode.NOT_FOUND_VOTE);
        }
        voteRepository.delete(voteOnGame.get());
    }
}
