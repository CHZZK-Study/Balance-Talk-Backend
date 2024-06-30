package balancetalk.file.dto;

import balancetalk.file.domain.File;
import balancetalk.file.domain.FileFormat;
import balancetalk.file.domain.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class FileResponse {
    @Schema(description = "파일 id", example = "1")
    private Long id;

    @Schema(description = "사용자가 업로드한 파일명", example = "사진1")
    private String uploadName;

    @Schema(description = "서버에 저장되는 파일명", example = "d23d2dqwt1251asbds사진1")
    private String storedName;

    @Schema(description = "업로드한 파일의 경로", example = "/...")
    private String path;

    @Schema(description = "파일이 사용되는 도메인", example = "TALK_PICK")
    private FileType fileType;

    @Schema(description = "업로드한 파일 확장자", example = "JPEG")
    private FileFormat fileFormat;

    @Schema(description = "업도르한 파일 사이즈", example = "236")
    private Long size;

    public File toEntity() {
        return File.builder()
                .uploadName(uploadName)
                .storedName(storedName)
                .path(path)
                .fileFormat(fileFormat)
                .size(size)
                .build();
    }

    public static FileResponse fromEntity(File file) {
        return FileResponse.builder()
                .id(file.getId())
                .uploadName(file.getUploadName())
                .storedName(file.getStoredName())
                .path(file.getPath())
                .fileType(file.getFileType())
                .fileFormat(file.getFileFormat())
                .size(file.getSize())
                .build();
    }
}
