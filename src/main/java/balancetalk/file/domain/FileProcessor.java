package balancetalk.file.domain;

import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileProcessor {

    public File process(MultipartFile multipartFile, FileType fileType) {
        String originalName = multipartFile.getOriginalFilename();
        String storedName = createRandomName(originalName);
        long size = multipartFile.getSize();
        String mimeType = multipartFile.getContentType();
        return createFile(originalName, storedName, fileType, mimeType, size);
    }

    private String createRandomName(String originalName) {
        return String.format("%s_%s", UUID.randomUUID(), originalName);
    }

    private File createFile(String uploadName,
                            String storedName,
                            FileType fileType,
                            String mimeType,
                            long size) {
        return File.builder()
                .uploadName(uploadName)
                .storedName(storedName)
                .fileType(fileType)
                .mimeType(mimeType)
                .size(size)
                .build();
    }
}
