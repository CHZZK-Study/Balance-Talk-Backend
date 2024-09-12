package balancetalk.game.application;

import static balancetalk.bookmark.domain.BookmarkType.GAME;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameOption;
import balancetalk.game.domain.GameReader;
import balancetalk.game.domain.GameSet;
import balancetalk.game.domain.MainTag;
import balancetalk.game.domain.repository.GameRepository;
import balancetalk.game.domain.repository.GameSetRepository;
import balancetalk.game.domain.repository.GameTagRepository;
import balancetalk.game.dto.GameDto.CreateGameMainTagRequest;
import balancetalk.game.dto.GameDto.CreateGameRequest;
import balancetalk.game.dto.GameDto.CreateGameSetRequest;
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

    private static final int PAGE_INITIAL_INDEX = 0;
    private static final int PAGE_LIMIT = 16;
    private static final int GAME_SIZE = 10;
    private final GameReader gameReader;
    private final GameRepository gameRepository;
    private final GameSetRepository gameSetRepository;
    private final MemberRepository memberRepository;
    private final GameTagRepository gameTagRepository;

    public void createBalanceGameSet(final CreateGameSetRequest request, final ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        MainTag mainTag = gameTagRepository.findByName(request.getMainTag())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_GAME_TOPIC));

        List<CreateGameRequest> gameRequests = request.getGames();

        if (gameRequests.size() < GAME_SIZE) {
            throw new BalanceTalkException(ErrorCode.BALANCE_GAME_SIZE_TEN);
        }

        GameSet gameSet = request.toEntity(mainTag, member);

        List<Game> games = gameSet.getGames();
        for (Game game : games) {
            game.addGameSet(gameSet);
            List<GameOption> gameOptions = game.getGameOptions();
            for (GameOption gameOption : gameOptions) {
                gameOption.addGame(game);
            }
        }
        gameSetRepository.save(gameSet);
    }

    public GameDetailResponse findBalanceGame(final Long gameId, final GuestOrApiMember guestOrApiMember) {
        Game game = gameReader.readById(gameId);
//        game.increaseViews();

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
        Pageable pageable = PageRequest.of(PAGE_INITIAL_INDEX, PAGE_LIMIT);
        List<Game> games = gameSetRepository.findGamesByCreationDate(topicName, pageable);

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
        Pageable pageable = PageRequest.of(PAGE_INITIAL_INDEX, PAGE_LIMIT);
        List<Game> games = gameSetRepository.findGamesByViews(topicName, pageable);

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
