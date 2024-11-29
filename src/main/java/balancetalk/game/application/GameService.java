package balancetalk.game.application;

import static balancetalk.file.domain.FileType.GAME_OPTION;

import balancetalk.bookmark.domain.GameBookmark;
import balancetalk.file.domain.File;
import balancetalk.file.domain.FileHandler;
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
import balancetalk.game.dto.GameSetDto.CreateGameSetRequest;
import balancetalk.game.dto.GameSetDto.GameSetDetailResponse;
import balancetalk.game.dto.GameSetDto.GameSetResponse;
import balancetalk.game.dto.GameSetDto.UpdateGameSetRequest;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.vote.domain.GameVote;
import balancetalk.vote.domain.VoteOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameSetRepository gameSetRepository;
    private final MemberRepository memberRepository;
    private final MainTagRepository mainTagRepository;
    private final FileRepository fileRepository;
    private final FileHandler fileHandler;

    @Transactional
    public Long createBalanceGameSet(final CreateGameSetRequest request, final ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        MainTag mainTag = mainTagRepository.findByName(request.getMainTag())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MAIN_TAG));

        List<CreateOrUpdateGame> gameRequests = request.getGames();
        GameSet gameSet = request.toEntity(mainTag, member);
        List<Game> games = new ArrayList<>();

        for (CreateOrUpdateGame gameRequest : gameRequests) {
            Game game = gameRequest.toEntity(fileRepository);
            games.add(game);
        }

        gameSet.addGames(games);
        gameSetRepository.save(gameSet);
        processFiles(request.getGames(), games);
        return gameSet.getId();
    }

    @Transactional
    public void updateBalanceGame(Long gameSetId, UpdateGameSetRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        MainTag mainTag = mainTagRepository.findByName(request.getMainTag())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MAIN_TAG));
        GameSet gameSet = member.getGameSetById(gameSetId);

        List<Game> newGames = request.getGames().stream()
                .map(gameRequest -> gameRequest.toEntity(fileRepository))
                .toList();

        gameSet.updateGameSetRequest(request.getTitle(), mainTag, request.getSubTag(), newGames);
        processFiles(request.getGames(), gameSet.getGames());
    }

    private void processFiles(List<CreateOrUpdateGame> gameRequests, List<Game> games) {
        for (int i = 0; i < gameRequests.size(); i++) {
            CreateOrUpdateGame gameRequest = gameRequests.get(i);
            Game game = games.get(i);

            for (int j = 0; j < gameRequest.getGameOptions().size(); j++) {
                GameOptionDto gameOptionDto = gameRequest.getGameOptions().get(j);
                GameOption gameOption = game.getGameOptions().get(j);

                if (gameOptionDto.getFileId() == null) {
                    continue;
                }

                fileRepository.findById(gameOptionDto.getFileId())
                        .ifPresent(
                                file -> fileHandler.relocateFiles(Collections.singletonList(file), gameOption.getId(),
                                        GAME_OPTION));

            }
        }
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public void deleteBalanceGameSet(final Long gameSetId, final ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        GameSet gameSet = member.getGameSetById(gameSetId);
        gameSetRepository.delete(gameSet);
        List<Long> gameOptionIds = gameSet.getGameOptionIds();
        deleteFiles(gameOptionIds);
    }

    private void deleteFiles(List<Long> gameOptionIds) {
        List<File> files = fileRepository.findAllByResourceIdsAndFileType(gameOptionIds, GAME_OPTION);
        if (files.isEmpty()) {
            return;
        }
        fileHandler.deleteFiles(files);
    }

    @Transactional(readOnly = true)
    public List<GameSetResponse> findLatestGames(final String tagName, final Pageable pageable,
                                                 final GuestOrApiMember guestOrApiMember) {
        List<GameSet> gameSets = gameSetRepository.findGamesByCreationDate(tagName, pageable);
        return gameSetResponses(guestOrApiMember, gameSets);
    }

    @Transactional(readOnly = true)
    public List<GameSetResponse> findPopularGamesWithTag(final String tagName, final Pageable pageable,
                                               final GuestOrApiMember guestOrApiMember) {
        List<GameSet> gameSets = gameSetRepository.findGamesByViews(tagName, pageable);
        return gameSetResponses(guestOrApiMember, gameSets);
    }

    @Transactional(readOnly = true)
    public List<GameSetResponse> findPopularGamesWithoutTag(final Pageable pageable, final GuestOrApiMember guestOrApiMember) {
        List<GameSet> popularGames = gameSetRepository.findPopularGames(pageable);
        return gameSetResponses(guestOrApiMember, popularGames);
    }

    private List<GameSetResponse> gameSetResponses(GuestOrApiMember guestOrApiMember, List<GameSet> gameSets) {
        if (guestOrApiMember.isGuest()) {
            return gameSets.stream()
                    .map(gameSet -> GameSetResponse.fromEntity(gameSet, null))
                    .toList();
        }
        Member member = guestOrApiMember.toMember(memberRepository);
        return gameSets.stream()
                .map(gameSet -> GameSetResponse.fromEntity(gameSet, member))
                .toList();
    }

    @Transactional
    public void createGameMainTag(final CreateGameMainTagRequest request, final ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        if (member.isRoleUser()) {
            throw new BalanceTalkException(ErrorCode.FORBIDDEN_MAIN_TAG_CREATE);
        }

        boolean hasGameTag = mainTagRepository.existsByName(request.getName());
        if (hasGameTag) {
            throw new BalanceTalkException(ErrorCode.ALREADY_REGISTERED_TAG);
        }
        MainTag mainTag = request.toEntity();
        mainTagRepository.save(mainTag);
    }
}
