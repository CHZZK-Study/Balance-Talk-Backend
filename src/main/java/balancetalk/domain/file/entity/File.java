package balancetalk.domain.file.entity;

import balancetalk.domain.Notice;
import balancetalk.domain.file.enums.FileType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
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
    private String uploadFileName; // 사용자가 업로드한 파일명

    @NotNull
    private String storedFileName; // 서버 내부에서 관리하는 파일명

    @NotNull
    @Size(max = 209)
    private String path;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private FileType type;

    @NotNull
    private Long size; // 바이트 단위로 파일 크기 저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;
}
