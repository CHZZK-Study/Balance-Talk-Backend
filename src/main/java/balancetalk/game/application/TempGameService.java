package balancetalk.game.application;

import static balancetalk.file.domain.FileType.TEMP_GAME;

import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.MainTag;
import balancetalk.game.domain.TempGame;
import balancetalk.game.domain.TempGameSet;
import balancetalk.game.domain.repository.GameTagRepository;
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
    private final FileRepository fileRepository;
    private final GameTagRepository gameTagRepository;

    @Transactional
    public void createTempGame(CreateTempGameSetRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        MainTag mainTag = gameTagRepository.findByName(request.getMainTag())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_GAME_TOPIC));

        List<String> storedNames = request.extractStoredNames();

        if (member.hasTempGameSet()) {
            Long tempGameSetId = member.updateTempGameSet(request.toEntity(mainTag, member));
            updateFileResourceIdByStoredNames(tempGameSetId, storedNames);
            return;
        }

        TempGameSet tempGameSet = request.toEntity(mainTag, member);
        List<TempGame> tempGames = tempGameSet.getTempGames();

        tempGameSet.addTempGames(tempGames);

        TempGameSet savedGameSet = tempGameSetRepository.save(tempGameSet);

        updateFileResourceIdByStoredNames(savedGameSet.getId(), storedNames);
    }

    private void updateFileResourceIdByStoredNames(Long resourceId, List<String> storedNames) {
        fileRepository.updateResourceIdAndTypeByStoredNames(resourceId, TEMP_GAME, storedNames);
    }

    public TempGameSetResponse findTempGameSet(ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        TempGameSet tempGameSet = tempGameSetRepository.findByMember(member)
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_BALANCE_GAME_SET));

        return TempGameSetResponse.fromEntity(tempGameSet);
    }
}
