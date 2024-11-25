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

    @Enumerated(value = EnumType.STRING)
    private FileType fileType;

    @NotBlank
    private String uploadName;

    @NotBlank
    private String storedName;

    @NotBlank
    private String mimeType;

    @NotNull
    @Positive
    private Long size;

    @NotBlank
    private String directoryPath;

    @NotBlank
    private String imgUrl;

    public void updateDirectoryPathAndImgUrl(String newDirectoryPath, String s3Endpoint) {
        this.directoryPath = newDirectoryPath;
        this.imgUrl = String.format("%s%s%s", s3Endpoint, newDirectoryPath, storedName);
    }

    public void updateResourceId(Long newResourceId) {
        this.resourceId = newResourceId;
    }

    public void updateFileType(FileType newFileType) {
        this.fileType = newFileType;
    }

    public String getS3Key() {
        return "%s%s".formatted(directoryPath, storedName);
    }

    public boolean isUnmapped() {
        return directoryPath.endsWith("temp/") && resourceId == null;
    }
}
