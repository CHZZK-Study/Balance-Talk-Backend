package balancetalk.game.application;

import static balancetalk.bookmark.domain.BookmarkType.GAME;
import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameTopic;
import balancetalk.game.domain.repository.GameRepository;
import balancetalk.game.domain.repository.GameTopicRepository;
import balancetalk.game.dto.GameDto.CreateGameRequest;
import balancetalk.game.dto.GameDto.CreateGameTopicRequest;
import balancetalk.game.dto.GameDto.GameDetailResponse;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.GuestOrApiMember;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

    @Slf4j
    @Service
    @Transactional
    @RequiredArgsConstructor
    public class GameService {

        private final GameRepository gameRepository;
        private final MemberRepository memberRepository;
        private final GameTopicRepository gameTopicRepository;

        public void createBalanceGame(CreateGameRequest request) {
            GameTopic gameTopic = gameTopicRepository.findByName(request.getName());
            if (gameTopic == null) {
                throw new BalanceTalkException(ErrorCode.NOT_FOUND_GAME_TOPIC);
            }
            Game game = request.toEntity(gameTopic);
            gameRepository.save(game);
        }

        public GameDetailResponse findBalanceGame(Long gameId, GuestOrApiMember guestOrApiMember) {
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME));
            game.increaseViews();

            if (guestOrApiMember.isGuest()) {
                return GameDetailResponse.from(game, false, null); // 게스트인 경우 북마크, 선택 옵션 없음
            }

            Member member = guestOrApiMember.toMember(memberRepository);
            boolean hasBookmarked = member.hasBookmarked(gameId, GAME);

            return GameDetailResponse.from(game, hasBookmarked, null);  // TODO: 투표 임시로 null 처리
        }

        public void createGameTopic(CreateGameTopicRequest request) {
            GameTopic gameTopic = request.toEntity();
            gameTopicRepository.save(gameTopic);
        }
    }