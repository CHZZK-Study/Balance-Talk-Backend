package balancetalk.game.domain;

import balancetalk.game.domain.repository.GameRepository;
import balancetalk.game.domain.repository.GameSetRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameReader {

    private final GameRepository gameRepository;
    private final GameSetRepository gameSetRepository;

    public GameSet findGameSetById(Long gameSetId) {
        return gameSetRepository.findById(gameSetId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME_SET));
    }

    public Game findGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME));
    }
}
