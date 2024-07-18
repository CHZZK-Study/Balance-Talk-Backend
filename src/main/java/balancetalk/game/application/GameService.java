package balancetalk.game.application;

import balancetalk.game.domain.GameTopic;
import balancetalk.game.domain.repository.GameTopicRepository;
import balancetalk.game.dto.GameDto.GameTopicCreateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

    @Slf4j
    @Service
    @Transactional
    @RequiredArgsConstructor
    public class GameService {

        private final GameTopicRepository gameTopicRepository;

        public void createGameTopic(GameTopicCreateRequest request) {
            GameTopic gameTopic = request.toEntity();
            gameTopicRepository.save(gameTopic);
        }
    }
