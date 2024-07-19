package balancetalk.game.application;

import balancetalk.game.domain.Game;
import balancetalk.game.domain.GameTopic;
import balancetalk.game.domain.repository.GameRepository;
import balancetalk.game.domain.repository.GameTopicRepository;
import balancetalk.game.dto.GameDto.CreateGameRequest;
import balancetalk.game.dto.GameDto.CreateGameTopicRequest;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
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
        private final GameTopicRepository gameTopicRepository;

        public void createBalanceGame(CreateGameRequest request) {
            GameTopic gameTopic = gameTopicRepository.findByName(request.getName());
            if (gameTopic == null) {
                throw new BalanceTalkException(ErrorCode.NOT_FOUND_GAME_TOPIC);
            }
            Game game = request.toEntity(gameTopic);
            gameRepository.save(game);
        }

        public void createGameTopic(CreateGameTopicRequest request) {
            GameTopic gameTopic = request.toEntity();
            gameTopicRepository.save(gameTopic);
        }
    }
