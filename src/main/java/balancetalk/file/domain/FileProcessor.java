package balancetalk.file.domain;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.UUID;

@Component
public class FileProcessor {

    public File process(MultipartFile multipartFile, String path, long resourceId, FileType fileType) {
        String originalName = multipartFile.getOriginalFilename();
        String storedName = createRandomName(originalName);
        long size = multipartFile.getSize();
        FileFormat FileFormat = convertMimeTypeToFileFormat(multipartFile.getContentType());
        return createFile(resourceId, originalName, storedName, FileFormat, path, fileType, size);
    }

    private String createRandomName(String originalName) {
        return String.format("%s_%s", UUID.randomUUID(), originalName);
    }

    private FileFormat convertMimeTypeToFileFormat(String mimeType) {
        return Arrays.stream(FileFormat.values())
                .filter(type -> type.getMimeType().equalsIgnoreCase(mimeType))
                .findFirst()
                .orElseThrow(() -> new BalanceTalkException(ErrorCode.NOT_SUPPORTED_FILE_FORMAT));
    }

    private File createFile(long resourceId,
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
