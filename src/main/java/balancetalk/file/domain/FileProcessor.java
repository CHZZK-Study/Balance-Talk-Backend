package balancetalk.file.domain;

import balancetalk.global.exception.BalanceTalkException;
import balancetalk.global.exception.ErrorCode;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileProcessor {

    public File process(MultipartFile multipartFile, FileType fileType) {
        String originalName = multipartFile.getOriginalFilename();
        String storedName = createRandomName(originalName);
        long size = multipartFile.getSize();
        FileFormat fileFormat = convertMimeTypeToFileFormat(multipartFile.getContentType());
        return createFile(originalName, storedName, fileFormat, fileType, size);
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

    private File createFile(String uploadName,
                            String storedName,
                            FileFormat fileFormat,
                            FileType fileType,
                            long size) {
        return File.builder()
                .uploadName(uploadName)
                .storedName(storedName)
                .fileFormat(fileFormat)
                .size(size)
                .fileType(fileType)
                .build();
    }
}
