package balancetalk.game.domain;

import balancetalk.game.dto.TempGameDto.CreateTempGameRequest;
import balancetalk.game.dto.TempGameOptionDto;
import balancetalk.game.dto.TempGameSetDto.CreateTempGameSetRequest;
import balancetalk.global.common.BaseTimeEntity;
import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import balancetalk.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempGameSet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "tempGameSet", cascade = CascadeType.ALL)
    private List<TempGame> tempGames = new ArrayList<>();

    @NotBlank
    @Size(max = 50)
    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_tag_id")
    private MainTag mainTag;

    @Size(max = 10)
    private String subTag;

    public void addGames(List<TempGame> tempGames) {
        this.tempGames = tempGames;
        tempGames.forEach(tempGame -> {
            tempGame.assignTempGameSet(this);
            tempGame.getTempGameOptions().forEach(option -> option.assignTempGame(tempGame));
        });
    }

    public void updateTempGameSet(String title, List<TempGame> newTempGames) {
        this.title = title;
        IntStream.range(0, this.tempGames.size()).forEach(i -> {
            TempGame existingGame = this.tempGames.get(i);
            TempGame newGame = newTempGames.get(i);
            existingGame.updateTempGame(newGame);
        });
    }

    public List<Long> getAllFileIds() {
        return tempGames.stream()
                .flatMap(game -> game.getTempGameOptions().stream())
                .map(TempGameOption::getImgId)
                .filter(Objects::nonNull)
                .toList();
    }

    public Map<Long, Long> getFileToOptionMap(CreateTempGameSetRequest request, List<Long> newFileIds) {
        Map<Long, Long> fileToOptionMap = new LinkedHashMap<>();
        List<CreateTempGameRequest> tempGameRequests = request.getTempGames();

        for (int i = 0; i < tempGameRequests.size(); i++) {
            CreateTempGameRequest gameRequest = tempGameRequests.get(i);

            List<TempGameOptionDto> tempGameOptions = gameRequest.getTempGameOptions();
            for (TempGameOptionDto optionDto : tempGameOptions) {
                Long fileId = optionDto.getFileId();

                if (fileId != null && newFileIds.contains(fileId)) {
                    TempGameOption tempGameOption = tempGames.get(i).getTempGameOptions()
                            .stream()
                            .filter(option -> option.getOptionType().equals(optionDto.getOptionType()))
                            .findFirst()
                            .orElseThrow(() -> new BalanceTalkException(ErrorCode.FILE_ID_GAME_OPTION_ID_MISMATCH));
                    fileToOptionMap.put(fileId, tempGameOption.getId());
                }
            }
        }
        return fileToOptionMap;
    }
}