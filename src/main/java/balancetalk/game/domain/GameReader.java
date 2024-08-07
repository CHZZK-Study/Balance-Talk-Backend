package balancetalk.game.domain;

import balancetalk.game.domain.repository.GameRepository;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameReader {

    private final GameRepository gameRepository;

    public Game readById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME));
    }
}
