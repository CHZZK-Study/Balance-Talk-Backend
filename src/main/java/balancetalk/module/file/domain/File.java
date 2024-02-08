package balancetalk.module.file.domain;

import balancetalk.module.Notice;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class File {

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String uploadName; // 사용자가 업로드한 파일명

    @Size(max = 50)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Builder
    public File(String uploadName, String path, FileType type, Long size) {
        this.uploadName = uploadName;
        this.path = path;
        this.type = type;
        this.size = size;
    }
}
