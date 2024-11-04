package balancetalk.file.domain;

import balancetalk.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class File extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long resourceId;

    @NotNull
    @Positive
    private Long size;

    @NotBlank
    private String uploadName;

    @NotBlank
    private String storedName;

    @Enumerated(value = EnumType.STRING)
    private FileType fileType;

    @Enumerated(value = EnumType.STRING)
    private FileFormat fileFormat;

    @NotBlank
    private String s3Key;

    @NotBlank
    private String s3Url;

    public void updateS3KeyAndUrl(String newS3Key, String newS3Url) {
        this.s3Key = newS3Key;
        this.s3Url = newS3Url;
    }

    public void updateResourceId(Long newResourceId) {
        this.resourceId = newResourceId;
    }

    public void updateFileType(FileType newFileType) {
        this.fileType = newFileType;
    }
}
