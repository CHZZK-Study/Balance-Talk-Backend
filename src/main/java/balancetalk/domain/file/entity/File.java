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
    @Max(50)
    private String name;

    @NotNull
    @Max(209)
    private String path;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private FileType type;

    @NotNull
    private Long size; // TODO 수정 필요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;
}
