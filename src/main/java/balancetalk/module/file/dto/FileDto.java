package balancetalk.module.file.dto;

import balancetalk.module.file.domain.File;
import balancetalk.module.file.domain.FileType;
import lombok.*;
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileDto {
    private String uploadName; // 사용자가 업로드한 파일명
    private String path;
    private FileType type;
    private Long size; // 바이트 단위로 파일 크기 저장

    public File toEntity() {
        return balancetalk.module.file.domain.File.builder()
                .uploadName(uploadName)
                .path(path)
                .type(type)
                .size(size)
                .build();
    }
}
