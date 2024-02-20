package balancetalk.module.file.dto;

import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileDto {
    @Schema(description = "사용자가 업로드한 파일명", example = "사진1")
    private String uploadName;
    @Schema(description = "업로드한 파일의 경로", example = "/...")
    private String path;
    @Schema(description = "업로드한 파일 확장자", example = "JPEG")
    private FileType type;
    @Schema(description = "업도르한 파일 사이즈", example = "236")
    private Long size; // 바이트 단위로 파일 크기 저장

    public File toEntity() {
        return File.builder()
                .uploadName(uploadName)
                .path(path)
                .type(type)
                .size(size)
                .build();
    }

    public static FileDto fromEntity(File file) {
        return FileDto.builder()
                .uploadName(file.getUploadName())
                .path(file.getPath())
                .type(file.getType())
                .size(file.getSize())
                .build();
    }
}
