package balancetalk.file.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class File {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String originalName; // 사용자가 업로드한 파일명

    @Size(max = 100)
    @Column(length = 50, unique = true)
    private String storedName; // 서버 내부에서 관리하는 파일명

    @NotBlank
    @Size(max = 209)
    @Column(nullable = false, length = 209)
    private String path;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private FileType type;

    @NotNull
    @Positive
    private Long size; // 바이트 단위로 파일 크기 저장

    public String getUrl() {
        return path + storedName;
    }
}
