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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    @NotNull
    @Size(max = 50)
    private String uploadName; // 사용자가 업로드한 파일명

    @NotNull
    private String storedName; // 서버 내부에서 관리하는 파일명

    @NotNull
    @Size(max = 209)
    private String path;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private FileType type;

    @NotNull
    private Long size; // 바이트 단위로 파일 크기 저장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;
}
