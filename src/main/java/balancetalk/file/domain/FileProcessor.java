package balancetalk.file.domain;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.UUID;

@Component
public class FileProcessor {

    public File process(MultipartFile multipartFile, String path, Long resourceId, FileType fileType) {
        String originalName = multipartFile.getOriginalFilename();
        String storedName = String.format("%s_%s", UUID.randomUUID(), originalName);
        long size = multipartFile.getSize();
        FileFormat FileFormat = convertMimeTypeToFileFormat(multipartFile.getContentType());
        return createFile(resourceId, originalName, storedName, FileFormat, path, fileType, size);
    }

    private FileFormat convertMimeTypeToFileFormat(String mimeType) {
        if (mimeType == null) {
            throw new IllegalArgumentException("MIME 타입은 NULL이 될 수 없습니다.");
        }

        return Arrays.stream(FileFormat.values())
                .filter(type -> type.getMimeType().equalsIgnoreCase(mimeType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 파일 타입 : " + mimeType));
    }

    private File createFile(Long resourceId,
                            String uploadName,
                            String storedName,
                            FileFormat FileFormat,
                            String path,
                            FileType fileType,
                            long size) {
        return File.builder()
                .resourceId(resourceId)
                .uploadName(uploadName)
                .storedName(storedName)
                .fileFormat(FileFormat)
                .path(path)
                .size(size)
                .fileType(fileType)
                .build();
    }
}
