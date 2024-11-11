package balancetalk.game.application;

import balancetalk.bookmark.domain.GameBookmark;
import balancetalk.file.domain.File;
import balancetalk.file.domain.FileHandler;
import balancetalk.file.domain.FileType;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameOption;
import balancetalk.game.domain.GameSet;
import balancetalk.game.domain.MainTag;
import balancetalk.game.domain.repository.GameSetRepository;
import balancetalk.game.domain.repository.MainTagRepository;
import balancetalk.game.dto.GameDto.CreateGameMainTagRequest;
import balancetalk.game.dto.GameDto.CreateOrUpdateGame;
import balancetalk.game.dto.GameDto.GameDetailResponse;
import balancetalk.game.dto.GameOptionDto;
import balancetalk.game.dto.GameSetDto.CreateGameSet;
import balancetalk.game.dto.GameSetDto.GameSetDetailResponse;
import balancetalk.game.dto.GameSetDto.GameSetResponse;
import balancetalk.game.dto.GameSetDto.UpdateGameSet;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.vote.domain.GameVote;
import balancetalk.vote.domain.VoteOption;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
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

    private final GameSetRepository gameSetRepository;
    private final MemberRepository memberRepository;
    private final MainTagRepository mainTagRepository;
    private final FileRepository fileRepository;
    private final FileHandler fileHandler;

    public void createBalanceGameSet(final CreateGameSet request, final ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        MainTag mainTag = mainTagRepository.findByName(request.getMainTag())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MAIN_TAG));
        String title = request.getTitle();

        List<CreateOrUpdateGame> gameRequests = request.getGames();
        if (gameRequests.size() < GAME_SIZE) {
            throw new BalanceTalkException(ErrorCode.BALANCE_GAME_SIZE_TEN);
        }

        GameSet gameSet = request.toEntity(title, mainTag, member);
        List<Game> games = new ArrayList<>();

        for (CreateOrUpdateGame gameRequest : gameRequests) {
            Game game = gameRequest.toEntity(fileRepository);
            games.add(game);
        }

        gameSet.addGames(games);
        gameSetRepository.save(gameSet);
        processFiles(request.getGames(), games);
    }

    public void updateBalanceGame(Long gameSetId, UpdateGameSet request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        MainTag mainTag = mainTagRepository.findByName(request.getMainTag())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MAIN_TAG));
        GameSet gameSet = member.getGameSetById(gameSetId);

        List<Game> newGames = request.getGames().stream()
                .map(gameRequest -> gameRequest.toEntity(fileRepository))
                .toList();

        gameSet.updateGameSet(request.getTitle(), mainTag, request.getSubTag(), newGames, fileRepository);
        gameSetRepository.save(gameSet);
        processFiles(request.getGames(), gameSet.getGames());
    }

    private void processFiles(List<CreateOrUpdateGame> gameRequests, List<Game> games) {
        IntStream.range(0, gameRequests.size()).forEach(i -> {
            CreateOrUpdateGame gameRequest = gameRequests.get(i);
            Game game = games.get(i);

            IntStream.range(0, gameRequest.getGameOptions().size()).forEach(j -> {
                GameOptionDto gameOptionDto = gameRequest.getGameOptions().get(j);
                GameOption gameOption = game.getGameOptions().get(j);

                List<File> files = fileRepository.findAllByS3Url(gameOptionDto.getImgUrl());
                fileHandler.relocateFiles(files, gameOption.getId(), FileType.GAME_OPTION);
            });
        });
    }

    public GameSetDetailResponse findBalanceGameSet(final Long gameSetId, final GuestOrApiMember guestOrApiMember) {
        GameSet gameSet = gameSetRepository.findById(gameSetId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME_SET));
        gameSet.increaseViews();

        if (guestOrApiMember.isGuest()) { // 비회원인 경우
            // 게스트인 경우 북마크, 선택 옵션 없음
            return GameSetDetailResponse.fromEntity(gameSet, null, false,
                    gameSet.getGames().stream()
                            .map(game -> GameDetailResponse.fromEntity(game, false, null))
                            .toList());
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        List<Game> games = gameSet.getGames();

        GameBookmark gameBookmark = member.getGameBookmarkOf(gameSet)
                .orElse(null);

        Map<Long, VoteOption> voteOptionMap = new ConcurrentHashMap<>();

        boolean isEndGameSet = (gameBookmark != null) && gameBookmark.getIsEndGameSet();

        for (Game game : games) {
            Optional<GameVote> myVote = member.getVoteOnGameOption(member, game);

            myVote.ifPresent(gameVote -> voteOptionMap.put(game.getId(), gameVote.getVoteOption()));
        }

        return GameSetDetailResponse.fromEntity(gameSet, gameBookmark, isEndGameSet,
                gameSet.getGames().stream()
                        .map(game -> GameDetailResponse.fromEntity(
                                game, isBookmarkedActiveForGame(gameBookmark, game), voteOptionMap.get(game.getId())))
                        .toList());
    }

    public boolean isBookmarkedActiveForGame(GameBookmark gameBookmark, Game game) {
        return gameBookmark != null
                && gameBookmark.getGameId().equals(game.getId())
                && gameBookmark.isActive();
    }

    public void deleteBalanceGameSet(final Long gameSetId, final ApiMember apiMember) {
        apiMember.toMember(memberRepository);
        gameSetRepository.deleteById(gameSetId);
    }

    public List<GameSetResponse> findLatestGames(final String tagName) {
        Pageable pageable = PageRequest.of(PAGE_INITIAL_INDEX, PAGE_LIMIT);
        List<GameSet> gameSets = gameSetRepository.findGamesByCreationDate(tagName, pageable);
        return gameSets.stream()
                .map(GameSetResponse::fromEntity).toList();
    }

    public List<GameSetResponse> findBestGames(final String tagName) {
        Pageable pageable = PageRequest.of(PAGE_INITIAL_INDEX, PAGE_LIMIT);
        List<GameSet> gameSets = gameSetRepository.findGamesByViews(tagName, pageable);
        return gameSets.stream()
                .map(GameSetResponse::fromEntity).toList();
    }

    public void createGameMainTag(final CreateGameMainTagRequest request, final ApiMember apiMember) {
        apiMember.toMember(memberRepository);
        boolean hasGameTag = mainTagRepository.existsByName(request.getName());
        if (hasGameTag) {
            throw new BalanceTalkException(ErrorCode.ALREADY_REGISTERED_TAG);
        }
        MainTag mainTag = request.toEntity();
        mainTagRepository.save(mainTag);
    }
}
