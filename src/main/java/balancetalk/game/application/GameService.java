package balancetalk.game.application;

import static balancetalk.bookmark.domain.BookmarkType.GAME;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameReader;
import balancetalk.game.domain.GameTopic;
import balancetalk.game.domain.repository.GameRepository;
import balancetalk.game.domain.repository.GameTopicRepository;
import balancetalk.game.dto.GameDto.CreateGameRequest;
import balancetalk.game.dto.GameDto.CreateGameTopicRequest;
import balancetalk.game.dto.GameDto.GameDetailResponse;
import balancetalk.game.dto.GameDto.GameResponse;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import balancetalk.member.dto.GuestOrApiMember;
import balancetalk.vote.domain.Vote;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final GameTopicRepository gameTopicRepository;

    public void createBalanceGame(CreateGameRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);

        GameTopic gameTopic = gameTopicRepository.findByName(request.getGameTopic())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_GAME_TOPIC));

        Game game = request.toEntity(gameTopic, member);

        gameRepository.save(game);
    }

    public GameDetailResponse findBalanceGame(Long gameId, GuestOrApiMember guestOrApiMember) {
        Game game = gameReader.readById(gameId);
        game.increaseViews();

        if (guestOrApiMember.isGuest()) {
            return GameDetailResponse.from(game, false, null); // 게스트인 경우 북마크, 선택 옵션 없음
        }

        Member member = guestOrApiMember.toMember(memberRepository);
        boolean hasBookmarked = member.hasBookmarked(gameId, GAME);
        Optional<Vote> myVote = member.getVoteOnGame(game);

        if (myVote.isEmpty()) {
            return GameDetailResponse.from(game, hasBookmarked, null); // 투표한 게시글이 아닌경우 투표한 선택지는 null
        }

        return GameDetailResponse.from(game, hasBookmarked, myVote.get().getVoteOption());
    }

    public List<GameResponse> findLatestGames(String topicName) {
        Pageable pageable = PageRequest.of(START_PAGE, END_PAGE);
        List<Game> games = gameRepository.findGamesByCreated(topicName, pageable);
        return games.stream()
                .map(game -> new GameResponse(game.getId(), game.getTitle(), game.getOptionA(), game.getOptionAImg(),
                        game.getOptionB(), game.getOptionBImg(), null))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<GameResponse> findBestGames(String topicName) {
        Pageable pageable = PageRequest.of(START_PAGE, END_PAGE);
        List<Game> games = gameRepository.findGamesByViews(topicName, pageable);
        return games.stream()
                .map(game -> new GameResponse(game.getId(), game.getTitle(), game.getOptionA(), game.getOptionAImg(),
                        game.getOptionB(), game.getOptionBImg() , topicName))
                .collect(Collectors.toUnmodifiableList());
    }

    public void createGameTopic(CreateGameTopicRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        GameTopic gameTopic = request.toEntity();
        gameTopicRepository.save(gameTopic);
    }
}
