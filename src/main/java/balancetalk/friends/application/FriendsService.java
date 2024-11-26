package balancetalk.friends.application;

import static balancetalk.friends.dto.FriendsDto.FriendsImageResponse;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileHandler;
import balancetalk.file.domain.FileType;
import balancetalk.file.domain.repository.FileRepository;
import balancetalk.friends.domain.Friends;
import balancetalk.friends.domain.FriendsRepository;
import balancetalk.friends.dto.FriendsDto.CreateFriendsRequest;
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
public class FriendsService {

    private final MemberRepository memberRepository;
    private final FriendsRepository friendsRepository;
    private final FileRepository fileRepository;
    private final FileHandler fileHandler;

    @Transactional
    public void createFriends(CreateFriendsRequest request, ApiMember apiMember) {
        Member member = apiMember.toMember(memberRepository);
        if (member.isRoleUser()) {
            throw new BalanceTalkException(ErrorCode.FORBIDDEN_PICK_O_FRIENDS_OPERATION);
        }
        Friends savedFriends = friendsRepository.save(request.toEntity());

        File file = fileRepository.findById(request.getImgId())
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_FOUND_FILE));
        fileHandler.relocateFile(file, savedFriends.getId(), FileType.FRIENDS);
    }

    public List<FriendsImageResponse> findAllFriendsImages() {
        return fileRepository.findAllById(getFriendsImgIds())
                .stream()
                .map(FriendsImageResponse::from)
                .toList();
    }

    private List<Long> getFriendsImgIds() {
        return friendsRepository.findAll()
                .stream()
                .map(Friends::getImgId)
                .toList();
    }
}
