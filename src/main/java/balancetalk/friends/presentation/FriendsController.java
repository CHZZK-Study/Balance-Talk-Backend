package balancetalk.friends.presentation;

import balancetalk.friends.application.FriendsService;
import balancetalk.friends.dto.FriendsDto.CreateFriendsRequest;
import balancetalk.global.utils.AuthPrincipal;
import balancetalk.member.dto.ApiMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
@Tag(name = "friends", description = "PICK-O 프렌즈 API")
public class FriendsController {

    private final FriendsService friendsService;

    @Operation(summary = "PICK-O 프렌즈 캐릭터 생성", description = "PICK-O 프렌즈 캐릭터를 생성합니다.")
    @PostMapping
    public void createCharacter(@RequestBody final CreateFriendsRequest request,
                                @Parameter(hidden = true) @AuthPrincipal final ApiMember apiMember) {
        friendsService.createFriends(request, apiMember);
    }
}
