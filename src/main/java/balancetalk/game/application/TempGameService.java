package balancetalk.game.application;

import static balancetalk.file.domain.FileType.TEMP_GAME_OPTION;
import static balancetalk.game.dto.TempGameDto.CreateTempGameRequest;

import balancetalk.file.domain.FileHandler;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.MainTag;
import balancetalk.game.domain.TempGame;
import balancetalk.game.domain.TempGameOption;
import balancetalk.game.domain.TempGameSet;
import balancetalk.game.domain.repository.MainTagRepository;
import balancetalk.game.domain.repository.TempGameSetRepository;
import balancetalk.game.dto.TempGameOptionDto;
import balancetalk.game.dto.TempGameSetDto.CreateTempGameSetRequest;
import balancetalk.game.dto.TempGameSetDto.TempGameSetResponse;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempGameService {

    private final MemberRepository memberRepository;
    private final TempGameSetRepository tempGameSetRepository;
    private final FileRepository fileRepository;
    private final FileHandler fileHandler;
    private final MainTagRepository mainTagRepository;

    @Transactional
    public void createTempGame(CreateTempGameSetRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        MainTag mainTag = mainTagRepository.findByName(request.getMainTag())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_MAIN_TAG));

        List<CreateTempGameRequest> tempGames = request.getTempGames();

        List<TempGame> newTempGames = tempGames.stream()
                .map(game -> game.toEntity(fileRepository))
                .toList();

        if (member.hasTempGameSet()) { // 기존 임시저장이 존재하는 경우
            TempGameSet existGame = member.getTempGameSet();
            existGame.updateTempGameSet(request.getTitle(), request.getSubTag(), mainTag, newTempGames);
            processTempGameFiles(tempGames, existGame.getTempGames());
            return;
        }

        TempGameSet tempGameSet = request.toEntity(mainTag, member);
        List<TempGame> games = new ArrayList<>();

        for (CreateTempGameRequest tempGame : tempGames) {
            TempGame game = tempGame.toEntity(fileRepository);
            games.add(game);
        }
        tempGameSet.addGames(games);
        tempGameSetRepository.save(tempGameSet);
        processTempGameFiles(tempGames, tempGameSet.getTempGames());
    }

    private void processTempGameFiles(List<CreateTempGameRequest> requests, List<TempGame> tempGames) {

        for (int i = 0; i < requests.size(); i++) {
            CreateTempGameRequest gameRequest = requests.get(i);
            List<TempGameOptionDto> tempGameOptions = gameRequest.getTempGameOptions();
            TempGame tempGame = tempGames.get(i);

            List<TempGameOption> gameOptions = tempGame.getTempGameOptions();

            for (int j = 0; j < tempGameOptions.size(); j++) {
                TempGameOptionDto tempGameOptionDto = tempGameOptions.get(j);
                TempGameOption tempGameOption = gameOptions.get(j);

                Long fileId = tempGameOptionDto.getFileId();
                Long gameOptionId = tempGameOption.getId();
                if (fileId != null) {
                    fileRepository.findById(fileId)
                        .ifPresent(file -> fileHandler.relocateFiles(Collections.singletonList(file),
                               gameOptionId, TEMP_GAME_OPTION));
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public TempGameSetResponse findTempGameSet(ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TempGameSet tempGameSet = tempGameSetRepository.findByMember(member)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME_SET));
        return TempGameSetResponse.fromEntity(tempGameSet, fileRepository);
    }
}
