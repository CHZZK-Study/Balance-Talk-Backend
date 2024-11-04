package balancetalk.game.application;

import static balancetalk.file.domain.FileType.TEMP_GAME;
import static balancetalk.game.dto.TempGameDto.CreateTempGameRequest;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileHandler;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.MainTag;
import balancetalk.game.domain.TempGame;
import balancetalk.game.domain.TempGameSet;
import balancetalk.game.domain.repository.GameTagRepository;
import balancetalk.game.domain.repository.TempGameRepository;
import balancetalk.game.domain.repository.TempGameSetRepository;
import balancetalk.game.dto.TempGameSetDto.CreateTempGameSetRequest;
import balancetalk.game.dto.TempGameSetDto.TempGameSetResponse;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempGameService {
    private final MemberRepository memberRepository;
    private final TempGameSetRepository tempGameSetRepository;
    private final TempGameRepository tempGameRepository;
    private final FileRepository fileRepository;
    private final GameTagRepository gameTagRepository;
    private final FileHandler fileHandler;

    @Transactional
    public void createTempGame(CreateTempGameSetRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        MainTag mainTag = gameTagRepository.findByName(request.getMainTag())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_GAME_TOPIC));

        if (member.hasTempGameSet()) {
            List<CreateTempGameRequest> tempGameRequests = request.getTempGames();

            TempGameSet tempGameSet = member.updateTempGameSet(request.toEntity(mainTag, member));
            List<TempGame> tempGames = tempGameSet.getTempGames();

            for (int i = 0; i < tempGameRequests.size(); i++) {
                CreateTempGameRequest tempGameRequest = tempGameRequests.get(i);
                List<File> files = fileRepository.findAllById(tempGameRequest.getFileIds());

                TempGame tempGame = tempGames.get(i);
                fileHandler.relocateFiles(files, tempGame.getId(), TEMP_GAME);
            }
            return;
        }

        TempGameSet tempGameSet = request.toEntity(mainTag, member);
        tempGameSetRepository.save(tempGameSet);

        List<CreateTempGameRequest> tempGameRequests = request.getTempGames();
        for (CreateTempGameRequest tempGameRequest : tempGameRequests) {
            TempGame tempGame = tempGameRequest.toEntity();
            tempGame.assignTempGameSet(tempGameSet);
            tempGameRepository.save(tempGame);
            List<File> files = fileRepository.findAllById(tempGameRequest.getFileIds());
            fileHandler.relocateFiles(files, tempGame.getId(), TEMP_GAME);

        }
    }

    public TempGameSetResponse findTempGameSet(ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TempGameSet tempGameSet = tempGameSetRepository.findByMember(member)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME_SET));

        return TempGameSetResponse.fromEntity(tempGameSet);
    }
}
