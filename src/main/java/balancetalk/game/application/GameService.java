package balancetalk.game.application;

import static balancetalk.bookmark.domain.BookmarkType.GAME;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameOption;
import balancetalk.game.domain.GameReader;
import balancetalk.game.domain.MainTag;
import balancetalk.game.domain.repository.GameRepository;
import balancetalk.game.domain.repository.GameTagRepository;
import balancetalk.game.dto.GameDto.CreateGameMainTagRequest;
import balancetalk.game.dto.GameDto.CreateGameRequest;
import balancetalk.game.dto.GameDto.GameDetailResponse;
import balancetalk.game.dto.GameDto.GameResponse;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.vote.domain.GameVote;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GameService {

    private static final int START_PAGE = 0;
    private static final int END_PAGE = 16;
    private final GameReader gameReader;
    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final GameTagRepository gameTagRepository;

    public void createBalanceGame(final CreateGameRequest request, final ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);

        MainTag mainTag = gameTagRepository.findByName(request.getMainTag())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_GAME_TOPIC));

        Game game = request.toEntity(mainTag, member);
        List<GameOption> gameOptions = game.getGameOptions();
        for (GameOption gameOption : gameOptions) {
            gameOption.addGame(game);
        }

        gameRepository.save(game);
    }

    public GameDetailResponse findBalanceGame(final Long gameId, final GuestOrApiMember guestOrApiMember) {
        Game game = gameReader.readById(gameId);
        game.increaseViews();

        if (guestOrApiMember.isGuest()) {
            return GameDetailResponse.from(game, false, null); // 게스트인 경우 북마크, 선택 옵션 없음
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        boolean hasBookmarked = member.hasBookmarked(gameId, GAME);

        Optional<GameVote> myVote = member.getVoteOnGameOption(member, game);

        if (myVote.isEmpty()) {
            return GameDetailResponse.from(game, hasBookmarked, null); // 투표한 게시글이 아닌경우 투표한 선택지는 null
        }

        return GameDetailResponse.from(game, hasBookmarked, myVote.get().getVoteOption());
    }

    public List<GameResponse> findLatestGames(final String topicName, GuestOrApiMember guestOrApiMember) {
        Pageable pageable = PageRequest.of(START_PAGE, END_PAGE);
        List<Game> games = gameRepository.findGamesByCreated(topicName, pageable);

        if (guestOrApiMember.isGuest()) {
            return games.stream()
                    .map(game -> GameResponse.fromEntity(game, null, false)).toList();
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        return games.stream()
                .map(game -> {
                    boolean bookmarked = member.hasBookmarked(game.getId(), GAME);
                    return GameResponse.fromEntity(game, member, bookmarked);
                }).toList();
    }

    public List<GameResponse> findBestGames(final String topicName, GuestOrApiMember guestOrApiMember) {
        Pageable pageable = PageRequest.of(START_PAGE, END_PAGE);
        List<Game> games = gameRepository.findGamesByViews(topicName, pageable);

        if (guestOrApiMember.isGuest()) {
            return games.stream()
                    .map(game -> GameResponse.fromEntity(game, null, false)).toList();
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        return games.stream()
                .map(game -> {
                    boolean bookmarked = member.hasBookmarked(game.getId(), GAME);
                    return GameResponse.fromEntity(game, member, bookmarked);
                }).toList();
    }

    public void createGameMainTag(final CreateGameMainTagRequest request, final ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        if (gameTagRepository.existsByName(request.getName())) {
            throw new BalanceTalkException(ErrorCode.ALREADY_REGISTERED_TAG);
        }
        MainTag mainTag = request.toEntity();
        gameTagRepository.save(mainTag);
    }
}
