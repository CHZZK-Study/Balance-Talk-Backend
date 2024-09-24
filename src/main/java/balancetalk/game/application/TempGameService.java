package balancetalk.game.application;

import static balancetalk.file.domain.FileType.TEMP_GAME;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.game.domain.TempGame;
import balancetalk.game.domain.repository.TempGameRepository;
import balancetalk.game.dto.TempGameDto.CreateTempGameRequest;
import balancetalk.member.domain.Member;
import balancetalk.member.domain.MemberRepository;
import balancetalk.member.dto.ApiMember;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempGameService {

    private final MemberRepository memberRepository;
    private final TempGameRepository tempGameRepository;
    private final FileRepository fileRepository;


    public void createTempGame(CreateTempGameRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);

//        if (member.hasTempGame()) {
//
//        }

        TempGame tempGame = tempGameRepository.save(request.toEntity(member));
        updateFileResourceIdByStoredNames(tempGame.getId(), request.getStoredNames());
    }

    private void updateFileResourceIdByStoredNames(Long resourceId, List<String> storedNames) {
        fileRepository.updateResourceIdAndTypeByStoredNames(resourceId, TEMP_GAME, storedNames);
    }
}
